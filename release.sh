#!/bin/bash

# Extract current version from main.go
CURRENT_VERSION=$(grep 'var AppVersion =' ./cs-server/main.go | cut -d'"' -f2)

# Increment version (assuming semantic versioning)
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

# Increment patch version
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="${MAJOR}.${MINOR}.${NEW_PATCH}"

# Update version in main.go
sed -i "s/var AppVersion = \"${CURRENT_VERSION}\"/var AppVersion = \"${NEW_VERSION}\"/" cs-server/main.go

# Commit changes
git add cs-server/main.go
git commit -m "Bump version to ${NEW_VERSION}"

# Push changes
git push -v origin

# Create and push tag
git tag  "v${NEW_VERSION}"
git push -v origin "v${NEW_VERSION}"

echo "Released version ${NEW_VERSION}"