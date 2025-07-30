package sfile

import (
	"encoding/json"
	"fmt"
	"math"
	"os"
	"path/filepath"
	"sync"
)

type Box struct {
	minx float64
	miny float64
	maxx float64
	maxy float64
}

var (
	parseRepositoryGroup sync.WaitGroup
)

func NewBox() Box {
	return Box{math.MaxFloat64, math.MaxFloat64, -math.MaxFloat64, -math.MaxFloat64}
}
func (b *Box) extend(b2 Box) {
	b.minx = min(b.minx, b2.minx)
	b.miny = min(b.miny, b2.miny)
	b.maxx = max(b.maxx, b2.maxx)
	b.maxy = max(b.maxy, b2.maxy)
}
func (b *Box) Set(minx float64, miny float64, maxx float64, maxy float64) {
	b.minx = minx
	b.miny = miny
	b.maxx = maxx
	b.maxy = maxy
}
func (b *Box) Empty() {
	b.minx = math.MaxFloat64
	b.miny = math.MaxFloat64
	b.maxx = -math.MaxFloat64
	b.maxy = -math.MaxFloat64
}
func (b *Box) IsEmpty() bool {
	return b.minx == math.MaxFloat64 && b.miny == math.MaxFloat64 && b.maxx == -math.MaxFloat64 && b.maxy == -math.MaxFloat64
}

type Repository struct {
	Name  string  `json:"name"`
	Lng   float64 `json:"lng"`
	Lat   float64 `json:"lat"`
	Zoom  int     `json:"zoom"`
	Size  float64 `json:"size"`
	Url   string  `json:"url"`
	Pared bool    `json:"pared"`
}

// UpdateMetaData
func UpdateMetaData(baseDir string, dirName string, repoData Repository) error {

	// Construct full path correctly
	fullPath := filepath.Join(baseDir, dirName, "repository.json")

	// Decode JSON
	jsonData, err := json.Marshal(repoData)
	if err != nil {
		return fmt.Errorf("failed to parse repository.json: %w", err)
	}
	err1 := os.WriteFile(fullPath, jsonData, 0644)
	if err1 != nil {
		return err1
	}
	return nil
}

// ListRepositories returns a list of available repositories
func ListRepositories(baseDir string) ([]Repository, error) {
	repositories := make([]Repository, 0)

	dirs, err := os.ReadDir(baseDir)
	if err != nil {
		return make([]Repository, 0), fmt.Errorf("仓库根目录不存在(%s)", baseDir)
	}

	for _, dir := range dirs {
		if dir.IsDir() {
			repo, err := readRepositoryInfo(baseDir, dir)
			if err != nil {
				// 统计仓库的信息 在一个新的线程中统计

				repo, err = analysisRepository(baseDir, dir)
				if err != nil {
					repositories = append(repositories, Repository{
						Name:  dir.Name(),
						Lng:   113.,
						Lat:   40.,
						Size:  0,
						Url:   dir.Name(),
						Pared: false,
						Zoom:  10,
					})
				} else {
					repositories = append(repositories, repo)
				}
			} else {
				repositories = append(repositories, repo)
			}
		}
	}
	return repositories, nil
}

func readRepositoryInfo(baseDir string, entry os.DirEntry) (Repository, error) {
	var repo Repository

	// Construct full path correctly
	fullPath := filepath.Join(baseDir, entry.Name(), "repository.json")

	// Open the file
	file, err := os.Open(fullPath)
	if err != nil {
		return repo, fmt.Errorf("failed to open repository.json: %w", err)
	}
	defer file.Close()

	// Decode JSON
	decoder := json.NewDecoder(file)
	if err := decoder.Decode(&repo); err != nil {
		return repo, fmt.Errorf("failed to parse repository.json: %w", err)
	}

	return repo, nil
}
