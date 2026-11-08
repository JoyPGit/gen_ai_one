

http client -> POST -> `OpenAiChatController` <-uses <- ChatClient (OpenAI implementation)
- use system message
- one shot prompt


dependencies
- starter-web
- spring-ai-starter-model-openai
- lombok


need to use in yaml
- openai `api key`
- model gpt-4o


how to replace application.properties with application.yaml?
- just replace with `application.yaml` and delete `application.properties`


spring version
- spring



gradle
- `gradlew build`
  ```Starting a Gradle Daemon, 1 incompatible Daemon could not be reused, use --status for details
     
     > Task :test FAILED
     
     AiProjOneApplicationTests > contextLoads() FAILED
         java.lang.IllegalStateException at DefaultCacheAwareContextLoaderDelegate.java:195
             Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException at ConstructorResolver.java:804
                 Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException at DefaultListableBeanFactory.java:2304
     
     1 test completed, 1 failed
     
     FAILURE: Build failed with an exception.
     
     * What went wrong:
  ```