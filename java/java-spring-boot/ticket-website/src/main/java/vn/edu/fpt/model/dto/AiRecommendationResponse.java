package vn.edu.fpt.model.dto;

import java.util.List;

public class AiRecommendationResponse {

    private String reply;
    private List<SuggestedEvent> suggestedEvents;
    private String recommendationType;

    public AiRecommendationResponse() {
    }

    public AiRecommendationResponse(String reply, List<SuggestedEvent> suggestedEvents, String recommendationType) {
        this.reply = reply;
        this.suggestedEvents = suggestedEvents;
        this.recommendationType = recommendationType;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<SuggestedEvent> getSuggestedEvents() {
        return suggestedEvents;
    }

    public void setSuggestedEvents(List<SuggestedEvent> suggestedEvents) {
        this.suggestedEvents = suggestedEvents;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public static class SuggestedEvent {
        private Long eventId;
        private String title;
        private String category;
        private String venue;
        private String date;
        private String matchReason;
        private String price;

        public SuggestedEvent() {
        }

        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getMatchReason() {
            return matchReason;
        }

        public void setMatchReason(String matchReason) {
            this.matchReason = matchReason;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}