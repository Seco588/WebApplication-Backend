/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tss.pw.secondi.posts;

import it.tss.pw.secondi.users.User;
import it.tss.pw.secondi.users.UserStore;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author 588se
 */
@RolesAllowed("users")
public class PostsResource {

    @Context
    ResourceContext resource;

    @Inject
    PostStore store;

    @Inject
    UserStore userStore;

    private Long userId;

    @PostConstruct
    public void init() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response all(@QueryParam("search") String search) {
       List<Post> result = search == null ? store.findByUsr(userId) : store.search(userId, search);
       return Response.ok(JsonbBuilder.create().toJson(result)).build();
    }

    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public PostResource find(@PathParam("id") Long id) {
        PostResource sub = resource.getResource(PostResource.class);
        sub.setId(id);
        sub.setUserId(userId);
        return sub;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(String json) {
        Post p = JsonbBuilder.create().fromJson(json, Post.class);
        User user = userStore.find(userId).orElseThrow(() -> new NotFoundException());
        p.setOwner(user);
        Post saved = store.create(p);
        return Response
                .status(Response.Status.CREATED)
                .entity(JsonbBuilder.create().toJson(saved))
                .build();
    }

    /*
    getter/setter
     */

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
