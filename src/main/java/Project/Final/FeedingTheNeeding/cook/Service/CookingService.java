package Project.Final.FeedingTheNeeding.cook.Service;

import Project.Final.FeedingTheNeeding.cook.DTO.Status;
import Project.Final.FeedingTheNeeding.cook.Exceptions.CookConstraintsNotExistException;
import Project.Final.FeedingTheNeeding.cook.Model.CookConstraints;
import Project.Final.FeedingTheNeeding.cook.Repository.CookConstraintsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CookingService {

    private final CookConstraintsRepository ccr;

    private static final Logger logger = LogManager.getLogger(CookingService.class);

    public CookingService(CookConstraintsRepository ccr){
        logger.info("Cooking service create");
        this.ccr = ccr;
        logger.info("Cooking service created");
    }

    public void log(String msg){
        logger.warn(msg);
    }
    public void log(long msg){
        logger.warn(msg);
    }

    public CookConstraints submitConstraints(CookConstraints constraints) {
        logger.info("Submit constraint of cook {} to date {}", constraints.getConstraintId(), constraints.getDate());
        return ccr.save(constraints);
    }

    public void updateConstraint(long constraintId, Map<String, Integer> constraint){
        logger.info("Trying to update constraint with id {}", constraintId);
        List<CookConstraints> constraints = ccr.findConstraintsByConstraintId(constraintId);
        CookConstraints cc = constraints.get(0);
        cc.setConstraints(constraint);
        ccr.save(cc);
        logger.info("Updated constraint with id {}", constraintId);
    }

    public void removeConstraint(CookConstraints constraint) {
        logger.info("removeConstraint of cook {} to date {}", constraint.getConstraintId(), constraint.getDate());
        long id = constraint.getConstraintId();
        LocalDate date = constraint.getDate();
        CookConstraints cookConstraint = ccr.findByConstraintIdAndDate(id, date).orElseThrow(() -> new CookConstraintsNotExistException(date));
        ccr.delete(cookConstraint);
        logger.info("removeConstraint of driver {} to date {} done", id, date);
    }

    public List<CookConstraints> getCookConstraints(long cookId) {
        logger.info("getCookConstraints of cook id= {}", cookId);
        List<CookConstraints> constraints = ccr.findConstraintsByCookId(cookId);
        logger.info("Successfully got all cook constraints");
        return constraints;
    }

    public List<CookConstraints> getLatestCookConstraints(long cookId, LocalDate date){
        logger.info("getCookConstraints of cook id= {}", cookId);
        List<CookConstraints> constraints = ccr.findConstraintsByCookId(cookId);
        List<CookConstraints> filteredConstraints = constraints.stream()
                .filter(constraint -> !constraint.getDate().isBefore(date))
                .collect(Collectors.toList());
        logger.info("Successfully got all cook constraints");
        return filteredConstraints;
    }

    public List<CookConstraints> getAcceptedCookByDate(LocalDate date){
        logger.info("Getting all cookers for the date {}", date);
        List<CookConstraints> constraints = ccr.findConstraintsByDateAndStatus(date, Status.Accepted);
        logger.info("Successfully got all constraints for the date: {}", date);
        return constraints;
    }

    public List<CookConstraints> getPendingConstraints(LocalDate date){
        logger.info("Getting all pending constraints for date: {}", date);
        List<CookConstraints> constraints = ccr.findConstraintsByDateAndStatus(date, Status.Pending);
        logger.info("Successfully got all pending constraints for the date: {}", date);
        return constraints;
    }

    public List<CookConstraints> getConstraintsByDate(LocalDate date){
        logger.info("Getting all pending constraints for date: {}", date);
        List<CookConstraints> constraints = ccr.findConstraintsByDate(date);
        logger.info("Successfully got all pending constraints for the date: {}", date);
        return constraints;
    }

    public CookConstraints changeStatusForConstraint(long constraintId, Status status){
        logger.info("Changing status for constraint No. {}", constraintId);
        logger.info("Desired updated status is: {}", status.getStatus());
        Optional<CookConstraints> constraint = ccr.findById(constraintId);
        if (constraint.isPresent()) {
            CookConstraints c = constraint.get();
            c.setStatus(status);
            CookConstraints temp = ccr.save(c);

            logger.info("Successfully updates constraint with id {}", constraintId);

            return temp;
        }

        throw new CookConstraintsNotExistException("There is no constraint with id such id");
    }
}
