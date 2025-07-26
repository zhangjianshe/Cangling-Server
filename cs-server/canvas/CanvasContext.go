package canvas

import (
	"bytes"
	"embed"
	"fmt" // Import fmt for error messages
	"github.com/golang/freetype/truetype"
	"golang.org/x/image/font"
	"golang.org/x/image/font/gofont/goregular"
	"golang.org/x/image/math/fixed" // For fixed-point arithmetic in font drawing
	"image"
	"image/color"
	"image/draw"
	"image/png" // For encoding PNG
	"log"       // For logging fatal errors during font loading
	"strings"
)

// CanvasContext holds resources needed for drawing operations, like the font.
type CanvasContext struct {
	Font font.Face // Exported field for the font face
}

// NewCanvasContext initializes and returns a new CanvasContext.
// It loads the default Go Regular font at a base size.
func NewCanvasContext(fs embed.FS) *CanvasContext {
	fontPath := "static/fonts/AlibabaPuHuiTi-3-65-Medium.ttf"
	fontBytes, err := fs.ReadFile(fontPath) // Read the font file into a byte slice
	if err != nil {
		// Log a warning if the custom font cannot be loaded and fall back to GoRegular.
		// This is a common point of failure for "index out of range" if the custom font is malformed.
		log.Printf("Warning: Cannot load custom font from %s. Falling back to goregular.TTF. Error: %v", fontPath, err)
		fontBytes = goregular.TTF // Use the embedded GoRegular font as a fallback
	}

	// Parse the font data
	parsedFont, err := truetype.Parse(fontBytes)
	if err != nil {
		// If parsing fails even for goregular.TTF (or a severely corrupted custom font),
		// this indicates a critical issue with the font data itself.
		// log.Fatalf will exit the program. Consider returning an error instead for a library.
		log.Fatalf("Failed to parse font data: %v", err)
	}

	// Create a font face (defines font size, DPI, etc.)
	faceOptions := &truetype.Options{
		Size:    18,               // Font size in points
		DPI:     72,               // Dots per inch
		Hinting: font.HintingNone, // No hinting for simplicity
	}
	fontFace := truetype.NewFace(parsedFont, faceOptions)
	// IMPORTANT: Do NOT defer fontFace.Close() here.
	// The fontFace needs to remain open for the lifetime of the CanvasContext
	// as it will be used for multiple drawing operations.
	// The caller of NewCanvasContext should manage closing the fontFace
	// if explicit resource cleanup is required (e.g., in a long-running server
	// where context might be destroyed). For most applications, it's fine
	// to let it be garbage collected when the program exits.

	return &CanvasContext{
		Font: fontFace, // Assign to the exported field
	}
}

// CreateImage creates an image with a specified background color and draws text on it.
// It returns the image data as a bytes.Buffer (PNG format) and an error if any.
func (c *CanvasContext) CreateImage(width int, height int, backgroundColor color.Color, textColor color.Color, text string) (bytes.Buffer, error) {
	var buf bytes.Buffer // Buffer to store the PNG image data

	imgRect := image.Rect(0, 0, width, height)
	img := image.NewRGBA(imgRect) // Create an RGBA image (supports transparency)

	// Fill the image with the specified background color
	draw.Draw(img, img.Bounds(), &image.Uniform{backgroundColor}, image.Point{}, draw.Src)

	// Create a font.Drawer to draw the text
	dr := &font.Drawer{
		Dst:  img,
		Src:  image.NewUniform(textColor),
		Face: c.Font, // Use the font face from the CanvasContext
	}

	lines := strings.Split(text, ":")
	lineHeight := 24
	y := fixed.I((height - (len(lines) * lineHeight)) / 2)
	for _, line := range lines {
		textWidth := dr.MeasureString(line)
		x := (fixed.I(width) - textWidth) / 2
		y = y + fixed.I(lineHeight+4)
		dr.Dot = fixed.Point26_6{X: x, Y: y}
		dr.DrawBytes([]byte(line))
	}

	// Encode the image to PNG format and write it to the buffer
	err := png.Encode(&buf, img)
	if err != nil {
		return bytes.Buffer{}, fmt.Errorf("failed to encode image to PNG: %w", err)
	}

	return buf, nil // Return the buffer containing the PNG data
}
