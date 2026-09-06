package com.kassidy.sales_tracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//controller tells spring that this class handles requests coming from website
import org.springframework.stereotype.Controller;
//Model allows us to send Java data to the HTML page
import org.springframework.ui.Model;
//gives us annotations like @GetMapping
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
//@controller tells spring that this class controls what happens when someone interacts with out website
@Controller
public class ProductController {
    //this var gives this controller access to the product database
    private final ProductRepository productRepository;

    //constructor for product controller. spring automatically gives us the ProductRepository
    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    //@getmapping means when someone visits the https page it runs this method
    @GetMapping("/")
    public String home(Model model){
        //gets every product currently stored in the database
        List<Product> products = productRepository.findAll();
        //these vars will store the totals for the entire store
        int totalItemsSold = 0;
        double totalRevenue = 0;

        //loop through ever products in our list
        for(Product product : products){
            totalItemsSold += product.getSoldCount();
            totalRevenue += product.getRevenue();
        }

        //these lines send java info to index.html. the html page can now access the list of products
        model.addAttribute("products", products);
        model.addAttribute("totalItemsSold", totalItemsSold);
        model.addAttribute("totalRevenue", totalRevenue);

        return "index";
    }

    //This method runs when the user submits
    // the "Add Product" form.
    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam double price,
                             @RequestParam(value = "image", required = false) MultipartFile image)
            throws IOException {

        // Create the new Product object.
        Product product = new Product(name, price);

        // Check whether the user actually selected an image.
        if (image != null && !image.isEmpty()) {

            // Create an "uploads" folder if it does not already exist.
            String uploadDirectory = "uploads/";

            File directory = new File(uploadDirectory);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Give the file a unique name so two images
            // with the same original filename do not overwrite each other.
            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + image.getOriginalFilename();

            // Save the image file into the uploads folder.
            Path filePath = Paths.get(uploadDirectory, fileName);

            Files.write(filePath, image.getBytes());

            // Save the path to the Product object.
            product.setImageUrl("/uploads/" + fileName);
        }

        // Save the Product object to the database.
        productRepository.save(product);

        return "redirect:/";
    }



    // This method runs when the user presses
    // the + button for a product.
    //
    // {id} represents the ID of whichever product
    // was clicked.
    @PostMapping("/plus/{id}")
    public String addSale(@PathVariable Long id) {


        // Searches the database for the Product
        // that has this specific ID.
        Product product =
                productRepository.findById(id).orElseThrow();


        // Calls the addSale() method from Product.java.
        //
        // quantitySold increases by 1.
        product.addSale();


        // Saves the updated Product back into the database.
        productRepository.save(product);


        // Reloads the main page.
        return "redirect:/";
    }



    // This method runs when the user presses
    // the - button for a product.
    @PostMapping("/minus/{id}")
    public String removeSale(@PathVariable Long id) {


        // Finds the correct Product using its ID.
        Product product =
                productRepository.findById(id).orElseThrow();


        // Calls removeSale() from Product.java.
        product.removeSale();


        // Saves the updated quantity to the database.
        productRepository.save(product);


        // Reloads the page.
        return "redirect:/";
    }



    // This method runs when the user edits a product.
    @PostMapping("/edit/{id}")
    public String editProduct(

            // Gets the ID of the product being edited.
            @PathVariable Long id,

            // Gets the new name from the HTML form.
            @RequestParam String name,

            // Gets the new price from the HTML form.
            @RequestParam double price) {


        // Finds the existing Product in the database.
        Product product =
                productRepository.findById(id).orElseThrow();


        // Uses setter methods from Product.java
        // to change the object's information.
        product.setName(name);
        product.setPrice(price);


        // Saves the edited Product back into the database.
        productRepository.save(product);


        // Reloads the main page.
        return "redirect:/";
    }



    // This method runs when the user presses
    // "Delete Product".
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {


        // Deletes the Product with this ID
        // from the database.
        productRepository.deleteById(id);


        // Reloads the main page.
        return "redirect:/";
    }
}
