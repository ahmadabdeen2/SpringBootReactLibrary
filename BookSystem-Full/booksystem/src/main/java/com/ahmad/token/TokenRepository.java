package com.ahmad.token;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {
  @Query(value = """
      select t from Token t inner join Users u on t.user = u.id where u.id = :id and (t.expired = false or t.revoked = false)
""")
  List<Token> findAllValidTokenByUser(Integer id);

  Optional<Token> findByToken(String token);
}