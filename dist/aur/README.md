# AUR Package: jtop

This directory contains everything needed for the Arch User Repository (AUR)
package of jtop.

## Files

- `PKGBUILD` – Arch Linux build recipe
- `.SRCINFO` – AUR metadata (regenerate with `makepkg --printsrcinfo > .SRCINFO`)
- `jtop.install` – Post-install message shown by pacman

## How it differs from direct `install.sh` usage

| Aspect | Direct install (`sudo ./install.sh`) | AUR package |
|--------|--------------------------------------|-------------|
| Updates | Built-in `--update` flag | pacman handles updates |
| Wrapper script | `jtop.sh` (from repo) with `--update` logic | Clean wrapper in PKGBUILD, no `--update` |
| Install location | `/usr/local/lib/jtop`, `/usr/local/bin/jtop` | Same paths |
| Config | Copied from `config/default.conf` | Same |

The `--update` flag is intentionally omitted from the AUR wrapper because
`pacman -Syu` is the proper update mechanism on Arch Linux. The `--update` flag
remains available in the repo's `jtop.sh` for users who install via `install.sh`.

## Publishing to AUR

1. Clone the AUR repository:
   ```
   git clone ssh://aur@aur.archlinux.org/jtop.git
   ```

2. Copy these files into the AUR repo:
   ```
   cp dist/aur/PKGBUILD dist/aur/.SRCINFO dist/aur/jtop.install /path/to/aur-repo/
   ```

3. Commit and push:
   ```
   cd /path/to/aur-repo
   git add PKGBUILD .SRCINFO jtop.install
   git commit -m "Initial jtop AUR package"
   git push
   ```
