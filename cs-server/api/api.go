package api

import (
	"SirServer/canvas" // Assuming canvas is a sibling package
	"SirServer/config"
	"SirServer/sfile" // Assuming sfile is a sibling package
	"bytes"
	"embed"
	"encoding/json"
	"fmt"
	"github.com/gorilla/mux"
	"image"
	"log"
	"net/http"
	"net/url"
	"path/filepath"
	"strconv"
	"sync"
	"time"
	"unicode/utf8"
)

// SirServer struct defines the server's metadata (moved here from main.go)
type SirServer struct {
	Name          string `json:"name"`
	Version       string `json:"version"`
	Author        string `json:"author"`
	Email         string `json:"email"`
	CompileTime   string `json:"compileTime"`
	GitHash       string `json:"gitHash"`
	LatestVersion string `json:"latestVersion"`
}

// ApiResult struct defines a standard API response format (moved here from main.go)
type ApiResult struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

// Ok creates a successful API result (moved here from main.go)
func Ok(data interface{}) ApiResult {
	return ApiResult{
		Code:    0,
		Message: "success",
		Data:    data,
	}
}

// Error creates an error API result (moved here from main.go)
func Error(code int, message string) ApiResult {
	return ApiResult{
		Code:    code,
		Message: message,
		Data:    nil,
	}
}

// WriteOk marshals data into a successful JSON response and writes it to the http.ResponseWriter (moved here)
func WriteOk(writer http.ResponseWriter, data interface{}) {
	writer.Header().Set("Content-Type", "application/json")
	result, err := json.Marshal(Ok(data))
	if err != nil {
		log.Printf("Error marshalling success response: %v", err)
		errorJson, _ := json.Marshal(Error(http.StatusInternalServerError, "Internal server error during response marshalling"))
		writer.WriteHeader(http.StatusInternalServerError)
		_, _ = writer.Write(errorJson)
		return
	}
	_, _ = writer.Write(result)
}

// WriteError marshals an error into a JSON response and writes it to the http.ResponseWriter (moved here)
func WriteError(writer http.ResponseWriter, code int, message string) {
	writer.Header().Set("Content-Type", "application/json")
	writer.WriteHeader(http.StatusOK)
	result, err := json.Marshal(Error(code, message))
	if err != nil {
		log.Printf("Error marshalling error response: %v", err)
		fallbackErrorJson, _ := json.Marshal(Error(http.StatusInternalServerError, "Internal server error"))
		_, _ = writer.Write(fallbackErrorJson)
		return
	}
	_, _ = writer.Write(result)
}

// WriteImage writes an image buffer as a PNG response (moved here)
func WriteImage(writer http.ResponseWriter, buffer bytes.Buffer) {
	writer.Header().Set("Content-Type", "image/png")
	writer.WriteHeader(http.StatusOK)
	writer.Header().Set("Content-Length", strconv.Itoa(len(buffer.Bytes())))
	_, _ = writer.Write(buffer.Bytes())
}
func WriteBytes(writer http.ResponseWriter, contentType string, buffer []byte) {
	writer.Header().Set("Content-Type", contentType)
	writer.WriteHeader(http.StatusOK)
	writer.Header().Set("Content-Length", strconv.Itoa(len(buffer)))
	_, _ = writer.Write(buffer)
}

// WriteHtml writes HTML content (moved here)
func WriteHtml(writer http.ResponseWriter, content []byte) {
	writer.Header().Set("Content-Type", "text/html; charset=utf-8")
	writer.WriteHeader(http.StatusOK)
	writer.Header().Set("Content-Length", strconv.Itoa(len(content)))
	_, _ = writer.Write(content)
}

// ApiContext holds dependencies for API handlers
type ApiContext struct {
	AppConfig     *config.Config
	SirServerInfo SirServer
	CanvasContext *canvas.CanvasContext // Note: canvas.CanvasContext is not an interface, so we pass the concrete type
	StaticFiles   *embed.FS
	EmptyTile     []byte   // empty tile data
	Repositories  sync.Map // hold a [repositoryPath]->SRepository cache the repository information
}

// NewApiContext creates and returns a new ApiContext
func NewApiContext(config *config.Config, serverInfo SirServer, canvasCtx *canvas.CanvasContext, staticFs *embed.FS) *ApiContext {
	emptyTileContent, err := staticFs.ReadFile("static/images/emptyTile.png")
	if err != nil {
		log.Fatalf("Error reading emptyTile file: %v", err)
	}
	return &ApiContext{
		AppConfig:     config,
		SirServerInfo: serverInfo,
		CanvasContext: canvasCtx,
		StaticFiles:   staticFs,
		EmptyTile:     emptyTileContent,
		Repositories:  sync.Map{},
	}
}

// FindRepository this method will cache the repository infomation
func (ac *ApiContext) FindRepository(key string, create bool) (*sfile.SRepository, error) {
	if utf8.RuneCountInString(key) == 0 {
		return nil, fmt.Errorf("请求的仓库PATH为空")
	}
	if value, ok := ac.Repositories.Load(key); ok {
		repo := value.(*sfile.SRepository)
		return repo, nil
	} else {
		repo := sfile.NewRepository(key, create)
		ac.Repositories.Store(key, repo)
		if repo.Ok {
			return repo, nil
		} else {
			buffer, err1 := ac.CanvasContext.CreateImage(256, 256, image.Transparent, image.Black, repo.Message)
			if err1 != nil {
				repo.ErrorTile = buffer.Bytes()
			} else {
				repo.ErrorTile = ac.EmptyTile
			}
			return nil, fmt.Errorf("%s", repo.Message)
		}
	}
}

// RegisterRoutes registers all API routes to the given mux router
func (ac *ApiContext) RegisterRoutes(r *mux.Router) {
	// API Routes
	r.HandleFunc("/api/v1/repositories", ac.listRepositoriesHandler).Methods("GET")
	r.HandleFunc("/api/v1/xyz/{dir}/update", ac.updateRepoMetaData).Methods("POST")
	r.HandleFunc("/api/v1/xyz/{dir}/{z:[0-9]+}/{x:[0-9]+}/{y:[0-9]+}.png", ac.xyzFileHandler).Methods("GET")
	r.HandleFunc("/api/v1/server", ac.serverInfoHandler).Methods("GET")
	staticFileDirectory := "static" // Path inside the embedded FS
	r.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		indexFile, _ := url.JoinPath(staticFileDirectory, "index.html")
		content, err := ac.StaticFiles.ReadFile(indexFile)
		if err != nil {
			WriteHtml(w, []byte("404 Not Found"))
			return
		}
		WriteHtml(w, content)
	})
}

// listRepositoriesHandler provides a list of available repositories
func (ac *ApiContext) listRepositoriesHandler(writer http.ResponseWriter, request *http.Request) {
	repositories, err := sfile.ListRepositories(ac.AppConfig.Repository.Root)
	if err != nil {
		WriteError(writer, http.StatusInternalServerError, err.Error())
		return
	}
	WriteOk(writer, repositories)
}

// xyzFileHandler processes requests for XYZ files
func (ac *ApiContext) xyzFileHandler(writer http.ResponseWriter, request *http.Request) {
	start := time.Now()
	vars := mux.Vars(request)
	fmt.Printf("Received request for XYZ: %v\n", vars)
	dirName := vars["dir"]
	x := vars["x"]
	y := vars["y"]
	z := vars["z"]
	dir := filepath.Join(ac.AppConfig.Repository.Root, dirName)
	NewSFile, err := ac.FindRepository(dir, false)
	if err != nil {
		// Use ac.CanvasContext
		fmt.Println("not find repository,", err.Error())
		WriteBytes(writer, "image/png", NewSFile.ErrorTile)
		return
	}
	intx, _ := strconv.ParseInt(x, 10, 64)
	inty, _ := strconv.ParseInt(y, 10, 64)
	intz, _ := strconv.ParseInt(z, 10, 8)

	xyz, err := NewSFile.GetXYZ(intx, inty, int8(intz))
	if err != nil {
		buffer, err1 := ac.CanvasContext.CreateImage(256, 256, image.Transparent, image.White, err.Error())
		if err1 != nil {
			WriteBytes(writer, "image/png", NewSFile.ErrorTile)
		} else {
			WriteImage(writer, buffer)
		}
		return
	}
	end := time.Now()
	log.Printf("read tile (Nano)%d", end.UnixMilli()-start.UnixMilli())
	WriteImage(writer, *xyz)
}

// serverInfoHandler provides information about the server
func (ac *ApiContext) serverInfoHandler(writer http.ResponseWriter, request *http.Request) {
	WriteOk(writer, ac.SirServerInfo)
}

// updateRepoMetaData provides information about the server
func (ac *ApiContext) updateRepoMetaData(writer http.ResponseWriter, request *http.Request) {
	var repoData sfile.Repository
	vars := mux.Vars(request)
	fmt.Printf("Received request for XYZ: %v\n", vars)
	dirName := vars["dir"]
	err := json.NewDecoder(request.Body).Decode(&repoData)

	if err != nil {
		WriteError(writer, 500, err.Error())
		return
	}
	err = sfile.UpdateMetaData(ac.AppConfig.Repository.Root, dirName, repoData)
	if err != nil {
		log.Printf(err.Error())
	}
	WriteOk(writer, repoData)
}
