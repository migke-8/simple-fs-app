{
  description = "Scala Multi-platform (JVM/JS) Development Environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
        };
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            # Java Runtime & Compiler
            openjdk21
            
            # Scala Tools
            scala-cli
            
            # LSP and Formatting
            metals
            scalafmt
            
            # JS/Browser Specifics
            nodejs
            nodePackages.npm

            # nix
            nixd
            alejandra
          ];

          shellHook = ''
            export JAVA_HOME=${pkgs.openjdk17.home}
            export PATH="$JAVA_HOME/bin:$PATH"
            echo "--- Scala Cross-Platform Dev Shell ---"
            echo "JDK: $(java -version 2>&1 | head -n 1)"
            echo "Node: $(node --version)"
            echo "Scalafmt: $(scalafmt --version)"
          '';
        };
      });
}
