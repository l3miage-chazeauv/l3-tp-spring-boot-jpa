package fr.uga.l3miage.library.authors;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/authors")
    @ResponseStatus(HttpStatus.OK)
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }

    @GetMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDTO author(@PathVariable("id") Long id) throws EntityNotFoundException {
        try{
            Author aut = this.authorService.get(id);

            return authorMapper.entityToDTO(aut);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO newAuthor(@RequestBody AuthorDTO author) {

        if(author.fullName().replaceAll("\\s", "").equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Author aut = authorMapper.dtoToEntity(author);
        Author athS = authorService.save(aut);

        return authorMapper.entityToDTO(athS);
    }

    @PutMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDTO updateAuthor(@RequestBody AuthorDTO author, @PathVariable("id") Long id){

        if(id == author.id()){
            try{
                var aut = this.authorService.get(id);
                aut.setFullName(author.fullName());

                return this.authorMapper.entityToDTO(aut);
            } catch(Exception e){
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            }
        } else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable("id") Long id) {
        try{
            this.authorService.get(id);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try{
            this.authorService.delete(id);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public Collection<BookDTO> books(Long authorId) {
        return Collections.emptyList();
    }

}
