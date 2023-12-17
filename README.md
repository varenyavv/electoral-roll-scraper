# Getting Started

### How To Run
* Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* Open a Terminal or Command Prompt and run this app in a Docker container
```docker run --name np-electoral-app -p 8080:8080 varenyavv/np-electoral-roll```
* Access Application on any web browser at `http://localhost:8080/api/swagger-ui/index.html`
* Press `CTRL+C` to stop the application. 
  * In case, it doesn't work, open a new terminal and execute ```docker rm -f np-electoral-app```

### Rendering CSV in MS Excel

CSV has Devnagri script character which may not render properly if CSV is opened in MS Excel.

* Click on the Data menu bar option.
* Click on the From Text icon.
* Navigate to the location of the file that you want to import. Click on the filename and then click on the Import
  button. The Text Import Wizard - Step 1 or 3 window will now appear on the screen.
* Choose the file type that best describes your data - Delimited or Fixed Width.

### Build App

```
./gradlew clean build
```

### Publish Image
```
docker build -t np-electoral-roll:1.0 .
docker tag np-electoral-roll:1.0 varenyavv/np-electoral-roll:1.0
docker push varenyavv/np-electoral-roll:1.0
```