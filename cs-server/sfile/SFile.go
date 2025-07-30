package sfile

import (
	"bytes"
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"os"
	"path/filepath"
)

type SRepository struct {
	dir       string
	Ok        bool
	Message   string
	ErrorTile []byte
}

// GetXYZ returns the content of the XYZ file
func (f *SRepository) GetXYZ(x int64, y int64, z int8) (*bytes.Buffer, error) {
	vz := max(z, 9)
	subDir := fmt.Sprintf("%c", 'A'+vz)
	dbFile := fmt.Sprintf("%c_%d_%d.s", 'A'+vz, x/256, y/256)
	filePath := filepath.Join(f.dir, subDir, dbFile)
	if _, err := os.Stat(filePath); os.IsNotExist(err) {
		log.Print(err)
		return nil, fmt.Errorf("没有数据")
	}
	db, err := sql.Open("sqlite3", filePath)
	if err != nil {
		log.Print(err)
		return nil, fmt.Errorf("没有数据")
	}
	defer db.Close()
	index := x%64 + 64*(y%64)
	tableName := fmt.Sprintf("%c_%d_%d", 'A'+vz, x/64, y/64)
	//log.Printf("%s/%c_%d_%d/%d\n", dbFile, 'A'+vz, x/64, y/64, index)
	selectSql := fmt.Sprintf("select Data from %s where ID=%d", tableName, index)
	log.Print("query clause ", selectSql)
	rows, err := db.Query(selectSql)
	if err != nil {
		log.Print("query error ", err.Error())
		return nil, fmt.Errorf("EMPTY")
	}
	defer rows.Close()
	if rows.Next() {
		var data []byte
		err = rows.Scan(&data)
		if err != nil {
			return nil, err
		}
		return bytes.NewBuffer(data), nil
	}
	return nil, fmt.Errorf("EMPTY")
}

// NewRepository creates a new SRepository
func NewRepository(dir string, created bool) *SRepository {
	info, err := os.Stat(dir)
	if os.IsNotExist(err) {
		if created {
			err := os.MkdirAll(dir, 0755)
			if err != nil {
				return &SRepository{
					dir:     dir,
					Ok:      false,
					Message: fmt.Sprintf("%s: create failed: %s", dir, err.Error()),
				}
			} else {
				return &SRepository{
					dir:     dir,
					Ok:      true,
					Message: fmt.Sprintf("%s: created", dir),
				}
			}
		}
		return &SRepository{
			dir:     dir,
			Ok:      false,
			Message: fmt.Sprintf("%s: not exist", dir),
		}
	}
	if info.IsDir() {
		return &SRepository{
			dir:     dir,
			Ok:      true,
			Message: fmt.Sprintf("%s: success", dir),
		}

	} else {
		return &SRepository{
			dir:     dir,
			Ok:      false,
			Message: fmt.Sprintf("%s: failed:is not a dir", dir),
		}
	}
}
