package fr.ecodeli.ecodelidesktop.services;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import fr.ecodeli.ecodelidesktop.api.ServicesAPI;
import fr.ecodeli.ecodelidesktop.controller.MainController;
import javafx.scene.shape.Circle;
import javafx.geometry.Insets;

import java.io.IOException;
import java.text.DecimalFormat;

public class ServiceDetailsController {

    @FXML private Label serviceNameLabel;
    @FXML private Label locationLabel;
    @FXML private Label durationLabel;
    @FXML private Label ratingLabel;
    @FXML private Label priceLabel;
    @FXML private Label serviceTypeLabel;
    @FXML private Label statusLabel;
    @FXML private Label availabilityLabel;
    @FXML private Label authorNameLabel;
    @FXML private Label authorEmailLabel;
    @FXML private Label authorRatingLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label commentsCountLabel;
    @FXML private ImageView authorAvatarImage;
    @FXML private Label authorAvatarPlaceholder;
    @FXML private FlowPane keywordsPane;
    @FXML private VBox commentsContainer;
    @FXML private VBox keywordsSection;
    @FXML private VBox commentsSection;

    private String serviceId;
    private ServicesAPI servicesAPI;
    private DecimalFormat priceFormat;

    public ServiceDetailsController() {
        this.servicesAPI = new ServicesAPI();
        this.priceFormat = new DecimalFormat("#0.00");
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
        loadServiceDetails();
    }

    private void loadServiceDetails() {
        try {
            ServiceDetails serviceDetails = servicesAPI.getServiceById(serviceId);
            populateServiceDetails(serviceDetails);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des détails du service");
        }
    }

    private void populateServiceDetails(ServiceDetails service) {
        serviceNameLabel.setText(service.getName() != null ? service.getName() : "Service sans nom");
        locationLabel.setText("📍 " + (service.getCity() != null ? service.getCity() : "Ville non spécifiée"));
        durationLabel.setText("🕐 " + service.getDurationTime() + " min");
        ratingLabel.setText("⭐ " + formatRating(service.getRate()));
        priceLabel.setText("€ " + priceFormat.format(service.getPrice()));

        serviceTypeLabel.setText(service.getServiceType() != null ? service.getServiceType() : "Type non spécifié");

        String status = service.getStatus() != null ? service.getStatus() : "Statut inconnu";
        statusLabel.setText(status);

        availabilityLabel.setText(service.isAvailable() ? "Disponible" : "Indisponible");

        updateStatusStyles(service.getStatus(), service.isAvailable());

        if (service.getAuthor() != null) {
            authorNameLabel.setText(service.getAuthor().getName() != null ? service.getAuthor().getName() : "Auteur inconnu");
            authorEmailLabel.setText(service.getAuthor().getEmail() != null ? service.getAuthor().getEmail() : "Email non disponible");
            authorRatingLabel.setText("⭐ " + formatRating(service.getRate()));

            loadAuthorPhoto(service.getAuthor().getPhoto());
        }

        descriptionLabel.setText(service.getDescription() != null ? service.getDescription() : "Aucune description disponible");

        populateKeywords(service);

        populateComments(service);

    }

    private void loadAuthorPhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            try {
                Image image = new Image(photoUrl, true);
                image.errorProperty().addListener((obs, oldError, newError) -> {
                    if (newError) {
                        showPlaceholderAvatar();
                    }
                });

                if (!image.isError()) {
                    authorAvatarImage.setImage(image);

                    double radius = 25;
                    Circle clip = new Circle(radius, radius, radius);
                    authorAvatarImage.setClip(clip);

                    authorAvatarImage.setVisible(true);
                    authorAvatarPlaceholder.setVisible(false);
                } else {
                    showPlaceholderAvatar();
                }
            } catch (Exception e) {
                showPlaceholderAvatar();
            }
        } else {
            showPlaceholderAvatar();
        }
    }

    private void showPlaceholderAvatar() {
        authorAvatarImage.setVisible(false);
        authorAvatarPlaceholder.setVisible(true);
    }

    private void populateKeywords(ServiceDetails service) {
        keywordsPane.getChildren().clear();
        if (service.getKeywords() != null && !service.getKeywords().isEmpty()) {
            for (String keyword : service.getKeywords()) {
                Label keywordLabel = new Label(keyword);
                keywordLabel.getStyleClass().add("keyword-tag");
                keywordsPane.getChildren().add(keywordLabel);
            }
            keywordsSection.setVisible(true);
        } else {
            keywordsSection.setVisible(false);
        }
    }

    private void populateComments(ServiceDetails service) {
        commentsContainer.getChildren().clear();
        if (service.getComments() != null && !service.getComments().isEmpty()) {
            commentsCountLabel.setText("💬 " + service.getComments().size());

            for (Comment comment : service.getComments()) {
                VBox commentBox = createCommentBox(comment);
                commentsContainer.getChildren().add(commentBox);
            }
            commentsSection.setVisible(true);
        } else {
            commentsCountLabel.setText("💬 0");
            Label noCommentsLabel = new Label("Aucun avis pour le moment");
            noCommentsLabel.getStyleClass().add("no-comments-label");
            commentsContainer.getChildren().add(noCommentsLabel);
        }
    }

    private VBox createCommentBox(Comment comment) {
        VBox commentBox = new VBox();
        commentBox.getStyleClass().add("comment-box");

        HBox commentHeader = new HBox();
        commentHeader.getStyleClass().add("comment-header");

        ImageView commentAvatar = new ImageView();
        commentAvatar.getStyleClass().add("comment-avatar-image");
        commentAvatar.setFitWidth(40);
        commentAvatar.setFitHeight(40);
        commentAvatar.setPreserveRatio(true);

        Circle clip = new Circle(20, 20, 20);
        commentAvatar.setClip(clip);

        Label avatarPlaceholder = new Label("👤");
        avatarPlaceholder.getStyleClass().add("comment-avatar-placeholder");

        VBox avatarContainer = new VBox();
        avatarContainer.getStyleClass().add("comment-avatar-container");

        if (comment.getAuthor() != null && comment.getAuthor().getPhoto() != null && !comment.getAuthor().getPhoto().trim().isEmpty()) {
            try {
                Image image = new Image(comment.getAuthor().getPhoto(), true);
                image.errorProperty().addListener((obs, oldError, newError) -> {
                    if (newError) {
                        commentAvatar.setVisible(false);
                        avatarPlaceholder.setVisible(true);
                    }
                });

                image.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() >= 1.0) {
                        if (!image.isError()) {
                            commentAvatar.setImage(image);
                            commentAvatar.setVisible(true);
                            avatarPlaceholder.setVisible(false);
                        } else {
                            commentAvatar.setVisible(false);
                            avatarPlaceholder.setVisible(true);
                        }
                    }
                });

                if (image.getProgress() >= 1.0) {
                    if (!image.isError()) {
                        commentAvatar.setImage(image);
                        commentAvatar.setVisible(true);
                        avatarPlaceholder.setVisible(false);
                    } else {
                        commentAvatar.setVisible(false);
                        avatarPlaceholder.setVisible(true);
                    }
                }
            } catch (Exception e) {
                commentAvatar.setVisible(false);
                avatarPlaceholder.setVisible(true);
            }
        } else {
            commentAvatar.setVisible(false);
            avatarPlaceholder.setVisible(true);
        }

        avatarContainer.getChildren().addAll(commentAvatar, avatarPlaceholder);

        VBox authorInfo = new VBox();
        Label authorName = new Label(comment.getAuthor() != null ? comment.getAuthor().getName() : "Utilisateur anonyme");
        authorName.getStyleClass().add("comment-author-name");
        authorInfo.getChildren().add(authorName);

        commentHeader.getChildren().addAll(avatarContainer, authorInfo);

        Label commentContent = new Label(comment.getContent() != null ? comment.getContent() : "Commentaire vide");
        commentContent.getStyleClass().add("comment-content");
        commentContent.setWrapText(true);

        commentBox.getChildren().addAll(commentHeader, commentContent);

        if (comment.getResponse() != null) {
            VBox responseBox = createResponseBox(comment.getResponse());
            VBox.setMargin(responseBox, new Insets(10, 0, 0, 20));
            commentBox.getChildren().add(responseBox);
        }

        return commentBox;
    }

    private VBox createResponseBox(Comment.Response response) {
        VBox responseBox = new VBox();
        responseBox.getStyleClass().add("response-box");

        HBox responseHeader = new HBox();
        responseHeader.getStyleClass().add("response-header");

        Label responseIcon = new Label("↳");
        responseIcon.getStyleClass().add("response-icon");

        Label authorName = new Label("Réponse de " + (response.getAuthor() != null ? response.getAuthor().getName() : "Auteur"));
        authorName.getStyleClass().add("response-author-name");

        responseHeader.getChildren().addAll(responseIcon, authorName);

        Label responseContent = new Label(response.getContent() != null ? response.getContent() : "Réponse vide");
        responseContent.getStyleClass().add("response-content");
        responseContent.setWrapText(true);

        responseBox.getChildren().addAll(responseHeader, responseContent);
        return responseBox;
    }

    private void updateStatusStyles(String status, boolean available) {
        statusLabel.getStyleClass().removeAll("status-active", "status-inactive", "status-pending");
        availabilityLabel.getStyleClass().removeAll("available", "unavailable");

        if (status != null) {
            switch (status.toLowerCase()) {
                case "active":
                case "actif":
                    statusLabel.getStyleClass().add("status-active");
                    break;
                case "inactive":
                case "inactif":
                    statusLabel.getStyleClass().add("status-inactive");
                    break;
                default:
                    statusLabel.getStyleClass().add("status-pending");
                    break;
            }
        }

        availabilityLabel.getStyleClass().add(available ? "available" : "unavailable");
    }

    private String formatRating(double rating) {
        if (rating == 0) return "Pas encore noté";
        return priceFormat.format(rating);
    }

    private void showError(String message) {
        serviceNameLabel.setText("Erreur");
        descriptionLabel.setText(message);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/ecodeli/ecodelidesktop/view/service/ServicesView.fxml"));
            Parent tableView = loader.load();

            ServiceTableController tableController = loader.getController();
            tableController.initialize();

            MainController.setContent(tableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
