DOCKER_NETWORK = awscronjob_default

build:
	mvn clean install
	docker-compose --env-file ./Env/test.env -f docker-compose-backend.yml up -d
	docker-compose ps

buildFront:
	cd frontend && npm run build
	docker-compose -f docker-compose-frontend.yml up -d

startFront:
	cd frontend && npm start

destroy:
	docker-compose -f docker-compose-backend.yml -f docker-compose-frontend.yml down
	docker volume prune  -f
	docker container prune  -f
