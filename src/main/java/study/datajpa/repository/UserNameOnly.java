package study.datajpa.repository;


import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {
    String getUserName();

    @Value("#{target.userName + ' ' + target.age}")
    String getMemberUserNameAndAge();
}
