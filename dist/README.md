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

## Policy: Who updates packages

Only the project owner (**JGH0**) pushes updates to package managers (AUR, etc.).
If you find an outdated package or need a new version pushed to a package manager, open an issue on GitHub or contact JGH0 directly — do not fork and push package manager updates yourself.

Pull requests improving the build recipes in `dist/` are welcome, but the actual upload to any package manager repository is done exclusively
by JGH0 or someone with the necessary permissions.

## Adding new formats

Contributions welcome! Each subdirectory should have:

- Build recipe files (PKGBUILD, Dockerfile, spec file, etc.)
- A brief README explaining what goes where
- The policy above applies: improvements to the recipe = good, pushing to a package manager repo = JGH0 only
