# Report

The installation and running of Gradle and Spring Boot went without any issues, and everything worked alright.

As enterprise app development isn’t my main focus at my home university, many of the concepts were new to me.

First, I tried learning the very basics of Boot, Java Beans, and dependency injection.

While working on the assignment, I mainly struggled with the project and file organization. With the help of many internet guides and forums, as well as ChatGPT, I hope I successfully achieved a correct and working project structure. I decided to split the main manager into three separate controllers to better organize everything.

Coding the models and business logic wasn’t that hard, but it did consume some time. Lastly, I struggled a bit with figuring out the de/serialization cyclic dependencies problem with the ‘@’ annotations, which I successfully managed to solve.

At the end, I created a few tests, let ChatGPT generate a few more, and reviewed them. The tests showed some minor issues in the logic and de/serialization, which I quickly fixed. Afterward I also set up these test in GitHub Actions.

In this current state, the application misses better error and null handling both in-app and in the API. This could be updated in the future.

Overall, this assignment gave me a nice overview of how the project structure should probably look, how Spring helps us while developing, and how to create and test a REST API.
