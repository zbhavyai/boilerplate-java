# Configure your environment: https://firebase.google.com/docs/studio/customize-workspace
{ pkgs, ... }: {
  channel = "stable-25.05";

  packages = [
    pkgs.jdk21
    pkgs.git
    pkgs.gnumake
    pkgs.docker
  ];

  services.docker.enable = true;

  env = { };

  idx = {
    extensions = [
      "dbaeumer.vscode-eslint"
      "esbenp.prettier-vscode"
      "foxundermoon.shell-format"
      "github.copilot-chat"
      "github.copilot"
      "humao.rest-client"
      "josevseb.google-java-format-for-vs-code"
      "ms-azuretools.vscode-containers"
      "ms-vscode-remote.remote-containers"
      "pkief.material-icon-theme"
      "redhat.java"
      "redhat.vscode-microprofile"
      "redhat.vscode-quarkus"
      "redhat.vscode-xml"
      "renesaarsoo.sql-formatter-vsc"
      "sonarsource.sonarlint-vscode"
      "visualstudioexptteam.intellicode-api-usage-examples"
      "visualstudioexptteam.vscodeintellicode"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-dependency"
      "vscjava.vscode-java-test"
      "vscjava.vscode-maven"
    ];

    previews = {
      enable = true;
      previews = {
        # web = {
        #   # Example: run "npm run dev" with PORT set to IDX's defined port for previews,
        #   # and show it in IDX's web preview panel
        #   command = ["npm" "run" "dev"];
        #   manager = "web";
        #   env = {
        #     # Environment variables to set for your server
        #     PORT = "$PORT";
        #   };
        # };
      };
    };

    workspace = {
      onCreate = {
        # Example: install JS dependencies from NPM
        # npm-install = "npm install";
      };
      onStart = {
        # Example: start a background task to watch and re-build backend code
        # watch-backend = "npm run watch-backend";
      };
    };
  };
}
