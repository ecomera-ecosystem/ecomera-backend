# ==========================================
# ECOMERA - MAKEFILE
# Automating Docker, Backend, Database, Tests, and More
# ==========================================

PROJECT = ecomera

.PHONY: help up up-d down restart logs ps build clean db-shell redis-cli pgadmin backend-shell logs-db logs-redis

# Default target
help:
	@echo "Ecomera Docker Commands:"
	@echo "  make up           - Start all services (foreground)"
	@echo "  make up-d         - Start all services (background)"
	@echo "  make down         - Stop all services"
	@echo "  make restart      - Restart all services"
	@echo "  make logs         - View all logs"
	@echo "  make ps           - List running containers"
	@echo "  make build        - Build Docker images"
	@echo "  make clean        - Remove all containers and volumes"
	@echo ""
	@echo "Service Shortcuts:"
	@echo "  make db-shell     - Connect to PostgreSQL"
	@echo "  make redis-cli    - Connect to Redis CLI"
	@echo "  make pgadmin      - Open pgAdmin in browser"
	@echo "  make backend-shell - Shell into backend container"
	@echo "  make logs-db      - View PostgreSQL logs"
	@echo "  make logs-redis   - View Redis logs"

# ------------------------------------------
# Docker Compose Commands
# ------------------------------------------

up:
	docker compose up --build

up-d:
	docker compose up -d --build

down:
	docker compose down

restart:
	docker compose down
	docker compose up -d --build

logs:
	docker compose logs -f

ps:
	docker compose ps

build:
	docker compose build

clean:
	docker compose down -v --remove-orphans
	docker system prune -f

# ------------------------------------------
# Service Shortcuts
# ------------------------------------------

db-shell:
	docker exec -it ecomera-db psql -U postgres -d ecomera

redis-cli:
	docker exec -it ecomera-redis redis-cli

pgadmin:
	@echo "Opening pgAdmin at http://localhost:5050"
	@command -v open >/dev/null 2>&1 && open http://localhost:5050 || \
	command -v xdg-open >/dev/null 2>&1 && xdg-open http://localhost:5050 || \
	echo "Please open http://localhost:5050 manually"

backend-shell:
	docker exec -it ecomera-backend sh

logs-db:
	docker compose logs -f postgres

logs-redis:
	docker compose logs -f redis

logs-backend:
	docker compose logs -f backend