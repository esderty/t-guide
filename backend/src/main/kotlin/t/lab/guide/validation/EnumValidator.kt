package t.lab.guide.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext

class EnumValidator : ConstraintValidator<ValidEnum, String> {
    private lateinit var values: Set<String>
    private lateinit var displayValues: String

    override fun initialize(constraintAnnotation: ValidEnum) {
        values =
            constraintAnnotation.enumClass.java.enumConstants
                .map { it.name }
                .toSet()
        displayValues = values.joinToString(", ")
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return true
        if (value in values) return true

        context
            .unwrap(HibernateConstraintValidatorContext::class.java)
            .addMessageParameter("enumValues", displayValues)
        return false
    }
}
