package bootcamp.hibernate_practical.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super ("Sorry, we can't find book with id " + id);
    }
}
