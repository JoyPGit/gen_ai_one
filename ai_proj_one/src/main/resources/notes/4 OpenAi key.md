

openai api key
- login api platform
- Steps
    - create api key
    - test models
    - add credits
    - key
      ```
      sk-proj-c0B-m3Z6oWAEHPseZ0ad8fNOM09hwic8T3***********************
      ********************
      ```
- click on Code on right top corner to get `curl`
- ```
  curl https://api.openai.com/v1/responses \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer sk-proj-c0B-m3Z6oWAE***********************" \
    -d '{
    "model": "gpt-6-luna",
    "service_tier": "default",
    "input": "is my api key working",
    "text": {
      "format": {
        "type": "text"
      },
      "verbosity": "medium"
    },
    "reasoning": {
      "effort": "medium",
      "mode": "standard",
      "summary": "auto"
    },
    "tools": [],
    "store": true,
    "include": [
      "reasoning.encrypted_content",
      "web_search_call.action.sources"
    ]
  }'
  ```

- sample response snippet
  ```
  "usage": {
      "input_tokens": 11,
      "input_tokens_details": {
        "cache_write_tokens": 0,
        "cached_tokens": 0
      },
      "output_tokens": 249,
      "output_tokens_details": {
        "reasoning_tokens": 119
      },
      "total_tokens": 260
    },
  ```

- 5$
- to pick value from terminal
    - set in `.zshrc`
    - `echo $OPENAI_API_KEY`