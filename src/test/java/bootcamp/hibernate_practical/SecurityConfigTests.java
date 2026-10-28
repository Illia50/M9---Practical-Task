package bootcamp.hibernate_practical;

import bootcamp.hibernate_practical.entity.Book;
import bootcamp.hibernate_practical.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityConfigTests {

    private static final String BOOK_JSON = """
            {"title": "Kobzar", "author": "Taras Shevchenko", "genre": "Poetry", "publicationYear": 1840}
            """;
    private static final String UPDATE_JSON = """
            {"title": "Kobzar", "author": "Taras Shevchenko", "genre": "Poetry", "publicationYear": 1840, "available": false}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private Long bookId;

    @BeforeEach
    void setUp() {
        bookId = bookRepository.save(new Book("1984", "George Orwell", "Dystopia", 1949, true)).getId();
    }

    @Test
    void getWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getWithWrongPasswordReturns401() throws Exception {
        mockMvc.perform(get("/api/books").with(httpBasic("user", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCanCallGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/books").with(httpBasic("user", "user")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/books/{id}", bookId).with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"));
        mockMvc.perform(get("/api/books/author/{author}", "george orwell").with(httpBasic("user", "user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("George Orwell"));
        mockMvc.perform(get("/api/books/available").with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanCallGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/books").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk());
    }

    @Test
    void userCannotCallNonGetEndpoints() throws Exception {
        mockMvc.perform(post("/api/books").with(httpBasic("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/books/{id}", bookId).with(httpBasic("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/books/{id}", bookId).with(httpBasic("user", "user")))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonGetWithoutCredentialsReturns401() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanCallNonGetEndpoints() throws Exception {
        mockMvc.perform(post("/api/books").with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Kobzar"))
                .andExpect(jsonPath("$.available").value(true));
        mockMvc.perform(put("/api/books/{id}", bookId).with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
        mockMvc.perform(delete("/api/books/{id}", bookId).with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());
    }

    @Test
    void missingBookReturns404() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999_999).with(httpBasic("user", "user")))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/books/{id}", 999_999).with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(UPDATE_JSON))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/books/{id}", 999_999).with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound());
    }
}
