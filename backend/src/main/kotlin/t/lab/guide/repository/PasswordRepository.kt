package t.lab.guide.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import t.lab.guide.domain.Password

interface PasswordRepository : CrudRepository<Password, Long> {
    @Modifying
    @Query("""insert into "password" (user_id, password_hash) values (:userId, :passwordHash)""")
    fun insertPassword(
        userId: Long,
        passwordHash: String,
    ): Int
}
