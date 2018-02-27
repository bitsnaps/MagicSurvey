package dz.crystalbox.magicsurvey.models

import com.arasthel.swissknife.annotations.Parcelable
import com.orm.dsl.Table
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@TupleConstructor
@ToString
@Parcelable
@CompileStatic
class User {

    static enum ROLE {
        ROLE_ADMIN, ROLE_USER
    }

    Long id
    Long expires_in
    String token_type
    String username
    String refresh_token
    String access_token
    List<ROLE> roles

//    def User(){}

    boolean hasRole(ROLE role){
        if (this.roles){
            this.roles.contains(role.toString())
        } else {
            false
        }
    }
}
