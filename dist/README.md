# dist/ — Distribution packages for various package managers

This directory contains packaging files for Linux distributions and other
package managers. Each subdirectory is named after the target format.

## Contents

| Directory | Format | Notes |
|-----------|--------|-------|
| `aur/` | Arch User Repository | PKGBUILD, .SRCINFO, jtop.install |

## Updating

When you push a new commit to the main repo, update the relevant packages:

- **AUR**: Update `pkgver` in `PKGBUILD` and `.SRCINFO` (or keep it VCS-based as-is)
- **New formats**: Add a new subdirectory here

## Adding new formats

Contributions welcome! Each subdirectory should have:

- Build recipe files (PKGBUILD, Dockerfile, spec file, etc.)
- A brief README explaining what goes where
