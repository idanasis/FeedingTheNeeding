package Project.Final.FeedingTheNeeding.User.Model;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StreetConverter implements AttributeConverter<Street, String> {

    @Override
    public String convertToDatabaseColumn(Street street) {
        return street != null ? street.getHebrewName() : null;
    }

    @Override
    public Street convertToEntityAttribute(String hebrewName) {
        return Street.fromHebrewName(hebrewName);
    }
}
