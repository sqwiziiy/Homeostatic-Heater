# Modrinth automatic publishing

GitHub Actions workflow: `.github/workflows/publish-modrinth.yml`

**One-time setup:**

1. Create a Modrinth project for this mod (if it does not exist). Copy its **project ID** from Modrinth.
2. Go to GitHub repository → **Settings → Secrets and variables → Actions**.
3. Under **Repository secrets**, create `MODRINTH_TOKEN` with a Modrinth personal API token that can create and edit project versions. Never put this token in a commit or share it in chat.
4. Under **Repository variables**, create `MODRINTH_PROJECT_ID` with the Modrinth project ID (not a token).
5. Set `mod_version` in `gradle.properties` (example `1.6.0`), merge the tested code into `main`, and publish a GitHub Release with tag `v1.6.0` on that commit.

On GitHub Release **published**, CI checks out the exact release tag, checks the Gradle version, builds the mod with JDK 17, runs unit tests, and uploads only the production JAR to Modrinth (Fabric 1.20.1). Homeostatic and Fabric API are marked as required dependencies. This does **not** modify the GitHub release or generate an additional release.

Without both the secret and the repository variable, the CI build still runs but skips uploading, with a warning. To publish an existing GitHub Release after you add credentials, use **Actions → Publish GitHub release to Modrinth → Run workflow** on `main`, and enter the matching existing tag, e.g. `v1.6.0`.

Before enabling unattended publishing, confirm Modrinth permissions and run a test release. Don't re-run a successful upload for the same version without checking whether Modrinth already has that version.
