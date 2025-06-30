package fr.ecodeli.ecodelidesktop.services;

public class Comment {
    private String id;
    private Author author;
    private String content;
    private Response response;

    public static class Author {
        private String id;
        private String name;
        private String photo;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPhoto() { return photo; }
        public void setPhoto(String photo) { this.photo = photo; }
    }

    public static class Response {
        private String id;
        private Author author;
        private String content;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Author getAuthor() { return author; }
        public void setAuthor(Author author) { this.author = author; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }
}
