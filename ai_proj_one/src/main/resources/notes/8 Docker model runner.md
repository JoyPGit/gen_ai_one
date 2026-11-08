
- where is docker file?
    - `find ~ -type f -name "Dockerfile" 2>/dev/null`

- multiple docker files? which one is being used?
    1. Open **Docker Desktop**.
    2. Click on **Containers** in the left sidebar.
    3. Find your running container or Compose stack in the list and **click on its name**.
    4. At the top of the container's detail screen, look for a tab named **Logs**, **Inspect**, or **Build**
    5. If you see a **Build** tab, click it. Docker Desktop will show you the exact build steps and often lists the source file name (e.g., `Dockerfile` or `Dockerfile.dev`) at the very top of the log sequence.

- install docker
- write a docker-compose file
- ```
  services:
    app:
      build: .
      ports:
        - "8081:8081"
  
    postgres:
      image: postgres:15
      environment:
        POSTGRES_USER: admin
        POSTGRES_PASSWORD: admin
        POSTGRES_DB: mydb
      ports:
        - "5432:5432"
  ```
- Here:
    - `app`: Docker builds an image using your project's `Dockerfile`.
    - `postgres`: Docker pulls the existing PostgreSQL image from Docker Hub.
    - You can see images under images, clicking on `play` creates docker `container`
    - You can run multiple containers from the same image.

Terminal
- docker images
- run `docker compose build`
-


**Docker model runner**
- Settings -> AI
- port default 12434
- Enable host side TCP Support
- CORS all -> `APPLY`
- allows to use models locally
- go to Models -> Local > Add modes -> choose one
    - If issues, check logs in the same window, diff tab
    - this worked `docker model pull hf.co/bartowski/google_gemma-3-1b-it-GGUF:Q4_K_M`
    - confirm in Local Models
- now configure `spring ai` to use this at port 12434
    - we update openai `base-url` to use
    - `http://localhost:12434/engines/llama.cpp`
        - why `llama.cpp`? docker model runner uses diff engines
        - we specify llama.cpp which loads and executes the gemma model which has been added to docker
        - `llama.cpp` supports many gguf
        - ollama also uses `llama.cpp` internally
    - `model`
        - to find model, run
            - `docker model status`
            - `docker model list`


