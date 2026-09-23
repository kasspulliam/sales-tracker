This is a mobile friendly sales tracker I built for s small festival vendor. It lets users add products, record sales, and see item and revenue totals while selling their products. 

FEATURES:
-add products with a name, price, and optional product image
-import product names and prices from an excel file
-increase or decrease each product's sales count
-view revenue by product and overall sales total
-edit/delete products
-keep products in separate browser session workspaces

BUILT WITH:
java, Spring Boot, Spring Data JPA, Thymeleaf, Apache POI, and an H2 database. The app is deployed on Railway.

SITE LINK:
https://sales-tracker-production-7535.up.railway.app/

HOW IT WORKS:
Each browser session receives a workspace ID. Products are saved with that ID so different sessions see separate product lists. This is session-based separation, not a user-account system. 
Workspaces are not designed to sync across browsers or devices, and access may be lost if the browser session ends.
