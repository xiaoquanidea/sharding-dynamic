rootProject.name = "Sharding-Dynamic"

include("sharding-api")
include("sharding-core")
include("sharding-starter")
include("sharding-samples")
include("sharding-xa")
include("sharding-samples:sharding-jta-sample")
findProject(":sharding-samples:sharding-jta-sample")?.name = "sharding-jta-sample"
