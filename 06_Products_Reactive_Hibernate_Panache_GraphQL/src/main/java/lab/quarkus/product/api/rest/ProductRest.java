package lab.quarkus.product.api.rest;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.quarkus.product.api.interfaces.ProductApi;
import lab.quarkus.product.entities.Product;
import lab.quarkus.product.services.ProductService;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductRest implements ProductApi {

  @Inject
  ProductService productService;


  @GET
  public Uni<List<Product>> getProductList() {
    return productService.getProductList();
  }

  @GET()
  @Path("/{id}")
  public Uni<Product> getProduct(@PathParam("id") Long id) {
    return productService.getProduct(id);
  }

  @PUT()
  @Path("/{id}")
  public Uni<Response> updateProduct(@PathParam("id") Long id, Product product) {
    return productService.updateProduct(id, product)
        .onItem().ifNotNull()
        .transform(entity -> Response.ok(entity).build())
        .onItem().ifNull()
        .continueWith(Response.ok().status(NOT_FOUND).build());
  }

  @POST
  public Uni<Response> addProduct(Product product) {
    return productService.addProduct(product)
        .replaceWith(Response.ok(product).status(CREATED)::build);
  }


  @DELETE
  @Path("/{id}")
  public Uni<Response> deleteProduct(@PathParam("id") Long id) {
    return productService.deleteProductById(id)
        .map(deleted -> {
          Response.Status deleteStatus = deleted ? NO_CONTENT : NOT_FOUND;
          return Response.ok().status(deleteStatus).build();
        });
  }
}
