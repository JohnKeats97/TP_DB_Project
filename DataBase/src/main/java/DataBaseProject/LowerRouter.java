package DataBaseProject;

import DataBaseProject.Functions.ForumFunctions;
import DataBaseProject.Functions.PostFunctions;
import DataBaseProject.Functions.ThreadFunctions;
import DataBaseProject.Functions.UserFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class LowerRouter {
    @Autowired
    protected UserFunctions userFunctions;
    @Autowired
    protected ForumFunctions forumFunctions;
    @Autowired
    protected ThreadFunctions threadFunctions;
    @Autowired
    protected PostFunctions postFunctions;
}
