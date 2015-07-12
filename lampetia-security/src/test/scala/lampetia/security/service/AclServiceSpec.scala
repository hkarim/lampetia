package lampetia.security.service

import java.util.UUID

import lampetia.model._
import lampetia.security.model._
import lampetia.security.module.SecurityTestModule._
import lampetia.test.LampetiaFutures
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

/**
 * @author Radwa Osama
 */
class AclServiceSpec extends FlatSpec with Matchers with ScalaFutures with LampetiaFutures {
  implicit val ec = executionContext

  val service = new AclService {}
  val groupService = new GroupService {}
  val userService = new UserService {}
  val roleService = new RoleService {}

  final val EMPTY = ""

  it should "create acl" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val acl = service.grant(aclData).run
        whenReady(acl, oneMinute) { result =>
          result.id.value shouldNot be(EMPTY)
        }

      }
    }
  }

  it should "create group with parent group" in {
    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val p = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(p, oneMinute) { parent =>
        parent.id.value shouldNot be(EMPTY)

        // Add child group
        def childGroupData =
          GroupData(code = Code(UUID.randomUUID.toString))
        val childGroup = groupService.createGroup(groupRef(owner.id, Some(parent.id)), childGroupData).run
        whenReady(childGroup, oneMinute) { child =>
          child.id.value shouldNot be(EMPTY)

          val subject = Subject(SubjectId(owner.id.value), SubjectUser)
          val resource = Resource(ResourceId(child.id.value), ResourceType("group"))
          val parentResource = Resource(ResourceId(parent.id.value), ResourceType("group"))

          val aclData = AclData(subject, resource, Some(parentResource), writePermission)

          val acl = service.grant(aclData).run
          whenReady(acl, oneMinute) { result =>
            result.id.value shouldNot be(EMPTY)
          }
        }
      }

    }
  }

  it should "find acl by id" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val aclr = service.grant(aclData).run
        whenReady(aclr, oneMinute) { acl =>
          acl.id.value shouldNot be(EMPTY)

          val sacl = service.findAclByAclId(acl.id).run

          whenReady(sacl, oneMinute) { result =>
            result shouldNot be(None)
            result.get.id.value should be(acl.id.value)
            result.get.data.resource.resourceId should be(acl.data.resource.resourceId)
            result.get.data.resource.resourceType should be(acl.data.resource.resourceType)
            result.get.data.subject.subjectId should be(acl.data.subject.subjectId)
            result.get.data.subject.subjectType should be(acl.data.subject.subjectType)
            result.get.data.permission.code should be(acl.data.permission.code)
          }
        }
      }
    }
  }

  it should "find all" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val aclr = service.grant(aclData).run
        whenReady(aclr, oneMinute) { acl =>
          acl.id.value shouldNot be(EMPTY)

          val all = service.findAll(10).run
          whenReady(all, oneMinute) { result =>
            result.size should be > 1
          }
        }
      }
    }
  }

  it should "check permission" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, writePermission)

        val acl = service.grant(aclData).run
        whenReady(acl, oneMinute) { result =>
          result.id.value shouldNot be(EMPTY)

          val hrp = service.hasPermission(subject.subjectId, resource.resourceId, readPermission).run
          whenReady(hrp, oneMinute) { pr =>
            pr should be(true)
          }

          val hwp = service.hasPermission(subject.subjectId, resource.resourceId, writePermission).run
          whenReady(hwp, oneMinute) { pr =>
            pr should be(true)
          }

          val hdp = service.hasPermission(subject.subjectId, resource.resourceId, deletePermission).run
          whenReady(hdp, oneMinute) { pr =>
            pr should be(false)
          }

        }

      }
    }

  }

  it should "revoke permission by Id" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val acl = service.grant(aclData).run
        whenReady(acl, oneMinute) { result =>
          result.id.value shouldNot be(EMPTY)

          val rp = service.revokePermission(result.id).run

          whenReady(rp, oneMinute) { rpr =>
            rpr should be(1)
          }
        }
      }
    }
  }

  it should "revoke specific permission for a subject on a resource" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val acl = service.grant(aclData).run
        whenReady(acl, oneMinute) { result =>
          result.id.value shouldNot be(EMPTY)

          val rp = service.rvokePermission(subject.subjectId, resource.resourceId, readPermission).run

          whenReady(rp, oneMinute) { rpr =>
            rpr should be(1)
          }
        }
      }
    }
  }

  it should "revoke all permissions for a subject on a resource" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val acl = service.grant(aclData).run
        whenReady(acl, oneMinute) { result =>
          result.id.value shouldNot be(EMPTY)

          val rp = service.revokePermission(subject.subjectId, resource.resourceId).run

          whenReady(rp, oneMinute) { rpr =>
            rpr should be(1)
          }
        }
      }
    }
  }

  it should "revoke all permissions for a subject" in {

    val u = userService.createUser(profileData).run
    whenReady(u, oneMinute) { owner =>
      def groupData =
        GroupData(code = Code(UUID.randomUUID.toString))
      val g = groupService.createGroup(groupRef(owner.id), groupData).run
      whenReady(g, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        val subject = Subject(SubjectId(owner.id.value), SubjectUser)
        val resource = Resource(ResourceId(group.id.value), ResourceType("group"))

        val aclData = AclData(subject, resource, None, readPermission)

        val acl = service.grant(aclData).run
        whenReady(acl, oneMinute) { result =>
          result.id.value shouldNot be(EMPTY)

          val rp = service.revokeAllPermissions(subject.subjectId).run

          whenReady(rp, oneMinute) { rpr =>
            rpr should be(1)
          }
        }
      }
    }
  }

  it should "succeed if subject has any of the input permissions" in {

    val subjectPermission = Permission(1 << 16)
    val groupPermission = Permission(1 << 10)
    val groupRolePermission = Permission(1 << 5)

    val userIO = userService.createUser(profileData).run
    whenReady(userIO, oneMinute) { user =>

      def groupData = GroupData(code = Code(UUID.randomUUID.toString))
      val groupIO = groupService.createGroup(groupRef(user.id), groupData).run

      whenReady(groupIO, oneMinute) { group =>
        group.id.value shouldNot be(EMPTY)

        // Add user as a member in the group
        val groupMemberIO = groupService.addMember(group.id, user.id).run

        whenReady(groupMemberIO, oneMinute) { groupMember =>
          val roleData = RoleData(Code("test-role-"), groupRolePermission)
          val createRoleIO = roleService.createRole(roleData).run

          whenReady(createRoleIO, oneMinute) { role =>

            val userSubject = Subject(SubjectId(user.id.value), SubjectUser)
            val resource = Resource(ResourceId(UUID.randomUUID().toString), ResourceType("com.nxt.entity:1.0"))
            val aclUserData = AclData(userSubject, resource, None, subjectPermission)

            val grantUserAclIO = service.grant(aclUserData).run

            whenReady(grantUserAclIO, oneMinute) { grantUserAcl =>

              val groupSubject = Subject(SubjectId(group.id.value), SubjectGroup)
              val aclGroupData = AclData(groupSubject, resource, None, groupPermission)
              val groupAclIO = service.grant(aclGroupData).run

              whenReady(groupAclIO, oneMinute) { groupAcl =>

                  val grantRoleIO = service.grant(groupSubject.subjectId, resource, role.id).run

                  whenReady(grantRoleIO, oneMinute) { grantRole =>
                    val seq =
                      Seq(
                        AclData(userSubject, resource, None, subjectPermission), // this one exists
                        AclData(userSubject, resource, None, Permission(1 << 11)), // this one doesn't
                        AclData(userSubject, resource, None, Permission(1 << 6))) // this one doesn't exist also

                    val hasAnyPermissionIO = service.hasAnyPermission(seq).run
                    whenReady(hasAnyPermissionIO, oneMinute) { hasAnyPermission =>
                      hasAnyPermission should be(true)
                    }
                  }

              }

            }
          }

        }

      }
    }

  }

  def groupRef(ownerId: UserId, parentGroupId: Option[GroupId] = None): GroupRef = GroupRef(ownerId, parentGroupId)

  def profileData = {
    val email = s"${UUID.randomUUID.toString}@test.org"
    ProfileData(
      UsernamePasswordProvider,
      ProviderUserId(""),
      ProviderResponse(PlayJson(Json.parse("[]"))),
      Email(email),
      Some(Password("unsafe")),
      AccountDetails(PlayJson(Json.parse("[]"))))
  }
}