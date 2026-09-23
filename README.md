# Walmart Scraping Challenge

A small Spring Boot application that scrapes selected category and product
information from the Walmart Mexico website and exposes the results through a
REST API.

## Features

- Retrieve Walmart Mexico categories up to the second category level.
- Retrieve products for supported Walmart category paths.
- Run locally with Maven or package the application into a Docker image.

## Requirements

Choose one of the following options:

### Local development

- Java 8
- Maven 3.5+ (or the Maven Wrapper included in `download/`)

### Docker

- Docker
- Internet access while building and running the application, because the
  scraper requests data from Walmart Mexico

## Project structure

```text
.
├── Dockerfile
├── README.md
└── download/
    ├── pom.xml
    ├── mvnw
    └── src/main/
        ├── java/com/challenge/download/
        │   ├── api/
        │   └── scraping/walmart/
        └── resources/
```

The application code is located in `download/`. The root-level `Dockerfile`
packages the JAR generated in `download/target/`.

## Run locally

From the repository root, build the application with the Maven Wrapper:

```bash
cd download
./mvnw clean package
```

Then start the generated JAR:

```bash
java -jar target/download-0.0.1-SNAPSHOT.jar
```

The API is available at `http://localhost:8080`.

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Run with Docker

Build the JAR first:

```bash
cd download
./mvnw clean package
cd ..
```

Build the Docker image from the repository root:

```bash
docker build -t walmart-scraping-challenge .
```

Start the container:

```bash
docker run --rm \
  --name walmart-scraping-challenge \
  -p 8080:8080 \
  walmart-scraping-challenge
```

View the application logs from another terminal:

```bash
docker logs -f walmart-scraping-challenge
```

The Docker image uses Java 8 and the `America/Mexico_City` time zone.

## API usage

### Get categories

```http
GET /category/
```

Example:

```bash
curl http://localhost:8080/category/
```

This endpoint requests Walmart categories and returns the scraped result.
Category availability depends on Walmart's current website structure.

### Get products for a category

```http
GET /product?category=<category-path>
```

The `category` value must be a supported Walmart category path. Examples:

```bash
curl --get 'http://localhost:8080/product' \
  --data-urlencode 'category=/lacteos/yogurt/yogurt-bebible'

curl --get 'http://localhost:8080/product' \
  --data-urlencode 'category=/lacteos/huevo/blanco'

curl --get 'http://localhost:8080/product' \
  --data-urlencode 'category=/bebidas-y-licores/cervezas/clara'
```

The application maps these paths to Walmart browse URLs. Not every Walmart
category is guaranteed to work because the site's category names and URL
structure are not completely consistent.

## Configuration

The application currently uses Spring Boot defaults and listens on port `8080`.
There are no required environment variables or external database settings.

JVM options can be supplied through `JAVA_OPTS` when running the Docker
container:

```bash
docker run --rm \
  --name walmart-scraping-challenge \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx1g -Dfile.encoding=UTF-8" \
  walmart-scraping-challenge
```

## Important limitations

- The scraper depends on Walmart Mexico's public website and may stop working
  if the website changes its HTML, URLs, or internal JSON structures.
- Product scraping supports only category paths recognized by the current
  implementation.
- Scraping can be slow and requires network access.
- The API currently returns plain string responses and reports many failures as
  `Bad Request`; callers should not assume every response is a structured error
  payload.
- No automated tests are currently included in the project.

## License

No license has been specified for this repository.
