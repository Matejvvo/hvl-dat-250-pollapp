//package no.hvl.dat250.pollapp;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//   /*
//   Use with:
//   - http://localhost:8080/hello?name=Matej
//   - http://localhost:8080/goodbye?name=Matej
//   - curl -X POST "http://localhost:8080/greet?name=Matej"
//   */
//
//@SpringBootApplication
//@RestController
//public class HelloWorld {
//    public static void main(String[] args) {
//        System.out.println("Hello World!");
//        SpringApplication.run(HelloWorld.class, args);
//    }
//
//    @GetMapping("/hello")
//    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return "Hello, " + name + "!\n";
//    }
//
//    @GetMapping("/goodbye")
//    public String goodbye(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return "Goodbye, " + name + "!\n";
//    }
//
//    @PostMapping("/greet")
//    public String greet(@RequestParam(value = "name", defaultValue = "World") String name) {
//        return "Greetings, " + name + "!\n";
//    }
//}
