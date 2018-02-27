package magicsurvey

class BootStrap {

    def init = { servletContext ->

        // Create some Roles
        def adminRole = new Role(authority: 'ROLE_ADMIN').save()
        def userRole = new Role(authority: 'ROLE_USER').save()
        // Create a user
        def adminUser = new User(username: 'admin', password: 'admin').save()
        // Assign a Role to User
        UserRole.create(adminUser, adminRole)
        UserRole.withSession {
           it.flush()
           it.clear()
        }
        
        def s = new Survey()
        s.title = 'Survey1'
        s.introMessage = 'Welcome'
        s.endMessage = 'Thank you'
        s.skipIntro = false
        s.userId = adminUser.id

        def q1 = new Question()
        q1.questionType = 'String'
        q1.questionTitle = 'What is your name'
        q1.description = 'Enter your name'
        q1.numberOfLines = 1
        q1.required = true

        def q2 = new Question(questionType:'Radioboxes',
                questionTitle:'How do you rate our product?',
                description:'Select one choice',
                required: false,
                randomChoices: false,
                choices: ['Good','OK','Bad']
        )

        s.addToQuestions(q1)
                .addToQuestions(q2)
                .save(flush: true)

        /*if (q1.validate() && s.validate()){
            println('ok')
            if (s.save(flush: true)){
                println('saved')
            } else {
                println('Error survey:')
                s.errors.allErrors.each {
                    println(it)
                }
                println('Error question:')
                q1.errors.allErrors.each {
                    println(it)
                }
            }
        } else {
            println(q1.errors)
            println(s.errors)
        }*/
        
    }
    def destroy = {
    }
}
