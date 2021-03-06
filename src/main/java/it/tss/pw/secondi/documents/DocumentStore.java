/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tss.pw.secondi.documents;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author 588se
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DocumentStore {
    
    @Inject
    @ConfigProperty(name = "documents.folder") //sto usando microprofile
    private String folder;
    
    @PersistenceContext(name = "pw")
    EntityManager em;
    
    @PostConstruct
    public void init() {
        
    }
    
    public List<Document> findByUserAndPost(Long userId, Long postId) {
        System.out.println("--------------------------------------------------------------Uso findByUserAndPost DocumentStore--------------------------------------------------------------");
        return em.createNamedQuery(Document.FIND_BY_USR_AND_POST, Document.class)
                .setParameter("userId", userId)
                .setParameter("postId", postId)
                .getResultList();
    }
    
    public Optional<Document> find(Long id) {
         System.out.println("--------------------------------------------------------------Uso find DocumentStore--------------------------------------------------------------");
        Document found = em.find(Document.class, id);
        return found == null ? Optional.empty() : Optional.of(found);
    }
    
    public Document save(Document d, InputStream is) {
        System.out.println("--------------------------------------------------------------Uso save DocumentStore--------------------------------------------------------------");
        Document saved = em.merge(d);
        try {
            System.out.println("--------------------------------------------------------------Uso saved.getFile() -> "+ saved.getFile() +"--------------------------------------------------------------");
            Files.copy(is, documentPath(saved.getFile()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new EJBException("save document failed...");
        }
        return saved;
    }
    
    public void remove(Document document) {
         System.out.println("--------------------------------------------------------------Uso remove 1 DocumentStore --------------------------------------------------------------");
        remove(document.getId());
    }

    public void remove(Long id) {
         System.out.println("--------------------------------------------------------------Uso remove 2 DocumentStore --------------------------------------------------------------");
         Document saved = find(id).orElseThrow(() -> new EJBException("Documento non trovato..."));
        try {
             System.out.println("--------------------------------------------------------------documentPath(saved.getFile()) -> " + documentPath(saved.getFile()).toString()+" \nsaved.getFile() -> "+saved.getFile().toString()+" --------------------------------------------------------------");
            Files.delete(documentPath(saved.getFile()));
        } catch (IOException ex) {
            throw new EJBException("delete document failed...");
        }
        em.remove(saved);
    }
    
    private Path documentPath(String file) {
         System.out.println("--------------------------------------------------------------Uso documentPath DocumentStore --------------------------------------------------------------");
         System.out.println("--------------------------------------------------------------Folder + file -> "+ folder +" + "+ file +"--------------------------------------------------------------");
            
        return Paths.get(folder + file);
    }
    
    public File getFile(String fileName) {
        return documentPath(fileName).toFile();
    }
    
}
