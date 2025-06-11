CONTAINER_ENGINE := $(shell if command -v podman &>/dev/null; then echo podman; else echo docker; fi)
DEPENDENCIES := $(CONTAINER_ENGINE) javac

.PHONY: check-deps clean dev build build-native run run-native container-build container-run container-stop container-logs container-destroy help

check-deps:
	@for cmd in $(DEPENDENCIES); do \
		if ! command -v $$cmd &>/dev/null; then \
			echo "Couldn't find $$cmd!"; \
			exit 1; \
		fi; \
	done

clean: check-deps
	@./mvnw --quiet clean;

dev: check-deps
	@./mvnw quarkus:dev

build: check-deps
	@./mvnw package -DskipTests

build-native: check-deps
	@./mvnw package -Dnative -DskipTests

run: check-deps
	@java -jar ./target/boilerplate-java-1.0.0-runner.jar

run-native: check-deps
	@./target/boilerplate-java-1.0.0-runner

container-build: check-deps
	@$(CONTAINER_ENGINE) compose build

container-run: check-deps
	@$(CONTAINER_ENGINE) compose up --detach

container-stop:
	@$(CONTAINER_ENGINE) compose down

container-logs:
	@$(CONTAINER_ENGINE) compose logs --follow

container-destroy:
	@$(CONTAINER_ENGINE) compose down --volumes --rmi local

help:
	@echo "Available targets:"
	@echo "  check-deps        - Check for required system dependencies"
	@echo "  clean             - Clean build artifacts"
	@echo "  dev               - Start app in development mode"
	@echo "  build             - Build app in JVM mode"
	@echo "  build-native      - Build app in native mode"
	@echo "  run               - Run app in JVM mode"
	@echo "  run-native        - Run app in native mode"
	@echo "  container-build   - Build app in containers and create container image"
	@echo "  container-run     - Run app container"
	@echo "  container-stop    - Stop app container"
	@echo "  container-logs    - Show app container logs"
	@echo "  container-destroy - Stop and delete app container, networks, volumes, and images"
	@echo "  help              - Show this help message"
