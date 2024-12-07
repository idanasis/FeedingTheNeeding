package Project.Final.FeedingTheNeeding.cook.Service;

import Project.Final.FeedingTheNeeding.cook.Exceptions.CookConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Repository.CookConstraintsRepository;
import Project.Final.FeedingTheNeeding.driving.Fascade.DrivingFascade;
import Project.Final.FeedingTheNeeding.driving.Model.DriverConstraint;
import Project.Final.FeedingTheNeeding.driving.exception.DriverConstraintsNotExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class CookingService {

    private final CookConstraintsRepository ccr;

    private static final Logger logger = LogManager.getLogger(CookingService.class);

    public CookingService(CookConstraintsRepository ccr){
        logger.info("Cooking service create");
        this.ccr = ccr;
        logger.info("Cooking service created");
    }

    public CookConstraints submitConstraints(CookConstraints constraints) {
        logger.info("Submit constraint of cook {} to date {}", constraints.getCookId(), constraints.getDate());
        return ccr.save(constraints);
    }

    public void removeConstraint(CookConstraints constraint) {
        logger.info("removeConstraint of cook {} to date {}", constraint.getCookId(), constraint.getDate());
        Long id = constraint.getCookId();
        LocalDate date = constraint.getDate();
        CookConstraints cookConstraint = ccr.findByCookIdAndDate(id, date).orElseThrow(() -> new CookConstraintsNotExistException(date));
        ccr.delete(cookConstraint);
        logger.info("removeConstraint of driver {} to date {} done", id, date);
    }

    public List<CookConstraints> getCookConstraints(long cookId) {
        logger.info("getCookConstraints of driver id {}", cookId);
        return ccr.findConstraintsByCookId(cookId);
    }

    public void updateCookConstraints(long cookId, CookConstraints newConstraints) {
        logger.info("Trying to update constraints of cook {}", cookId);
        CookConstraints constraints = getLastConstraints(cookId);
        logger.info("Got constraints from db");
        ccr.delete(constraints);
        logger.info("Deleted last constraints from db");
        ccr.save(newConstraints);
        logger.info("Successfully updated constraints for cook {}", cookId);
    }

    public CookConstraints getLastConstraints(long cookId){
        logger.info("Trying to get last constraints for cook {}", cookId);
        CookConstraints constraint = ccr.findConstraintsByCookId(cookId).stream()
                .max(Comparator.comparing(CookConstraints::getDate))
                .orElse(null);
        logger.info("Successfully got last constraints for cook {}", cookId);

        return constraint;
    }

    public List<CookConstraints> getCookHistory(long cookId) {
        logger.info("Trying to get all cook history for cook {}", cookId);
        List<CookConstraints> constraints = ccr.findConstraintsByCookId(cookId);
        logger.info("Successfully got all cook history");
        return constraints;
    }

    public List<CookConstraints> getAllCookOnDate(LocalDate date){
        logger.info("Getting all cookers for the date {}", date);
        List<CookConstraints> constraints = ccr.findConstraintsByDate(date);
        logger.info("Successfully got all constraints for the date: {}", date);
        return constraints;
    }
}
