package com.asm.authtest.requestmodels;

import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UsernameAndPassword implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameAndPassword that = (UsernameAndPassword) o;
        return this.username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
