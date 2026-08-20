package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Component
public class DisbursementVatAmountValidator implements FieldSpecificAmendmentValidator {

  public static final String ERROR_CODE = "amendmentForm.dates.disbursementVatExceeded";

  @Override
  public boolean appliesTo(ClaimViewField<?> field) {
    return ClaimDetailsViewField.DISBURSEMENTS_VAT.equals(field);
  }

  @Override
  public void validate(
      ClaimDetails claim, ClaimViewField<?> field, AmendmentForm form, Errors errors) {
    var disbursementsVatValue = form.getBigDecimalValue(field.name());
    var areaOfLaw = claim.getAreaOfLaw();

    if (disbursementsVatValue == null || areaOfLaw == null) {
      return;
    }

    BigDecimal maxAllowed =
        switch (areaOfLaw) {
          case LEGAL_HELP -> BigDecimal.valueOf(99999.99);
          case CRIME_LOWER -> BigDecimal.valueOf(999999.99);
          case MEDIATION -> BigDecimal.valueOf(9999999.99);
        };
    if (disbursementsVatValue.compareTo(maxAllowed) > 0) {
      addUniqueFieldError(field, ERROR_CODE, errors);
    }
  }
}
