package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.entity.Book;
import bootcamp.hibernate_practical.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(CreateBookRequest request) {
        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getGenre(),
                request.getPublicationYear(),
                true
        );
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        // TODO:
        // Fetch all books from the repository
        // Convert each Book entity into BookResponse DTO
        // Return the list
        List <Book> books = bookRepository.findAll();
        List <BookResponse> res = new ArrayList<>(books.size());
        for (Book book : books){
            res.add(mapToResponse(book));
        }
        return res;
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        // TODO
        // Find the book by its ID
        // Throw RuntimeException if not found
        // Convert the entity to BookResponse

        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Sorry, we can't find book with id: " + id));
        return mapToResponse(book);
    }

    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        // TODO
        // Find existing book
        // Update its fields
        // Save the updated entity
        // Convert to BookResponse
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Sorry, we can't find book with id: " + id));
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setGenre(request.getGenre());
        book.setPublicationYear(request.getPublicationYear());
        book.setAvailable(request.isAvailable());

        Book updateBook = bookRepository.save(book);
        return mapToResponse(updateBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        // TODO
        if (!bookRepository.existsById(id)) throw new RuntimeException("Sorry, we can't find book with id: " + id);
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findByAuthor(String author) {
        // TODO
        List <Book> books = bookRepository.findBooksByAuthorIgnoreCase(author);
        List <BookResponse> responses = new ArrayList<>();

        for (Book book : books){
            responses.add(mapToResponse(book));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAvailableBooks(){
        // TODO
        return bookRepository.findByAvailableTrue().stream().map(this::mapToResponse).toList();
    }

    private BookResponse mapToResponse(Book book) {
        // TODO: map Book to BookResponse
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPublicationYear(),
                book.isAvailable()
        );
    }
}
