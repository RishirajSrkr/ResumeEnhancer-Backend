package com.rishiraj.syncResume.entities;

import java.util.List;

public class ResumeData {
    private String name;
    private Contact contact;
    private String objective;
    private List<Project> projects;
    private List<String> skills;
    private List<WorkExperience> workExperience;
    private Education education;
    private List<String> achievements;

    // Default Constructor
    public ResumeData() {
    }

    // Parameterized Constructor
    public ResumeData(String name, Contact contact, String objective, List<Project> projects, List<String> skills,
                      List<WorkExperience> workExperience, Education education, List<String> achievements) {
        this.name = name;
        this.contact = contact;
        this.objective = objective;
        this.skills = skills;
        this.projects = projects;
        this.workExperience = workExperience;
        this.education = education;
        this.achievements = achievements;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<WorkExperience> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<WorkExperience> workExperience) {
        this.workExperience = workExperience;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public List<String> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<String> achievements) {
        this.achievements = achievements;
    }

    // toString Method
    @Override
    public String toString() {
        return "ResumeData{" +
                "name='" + name + '\'' +
                ", contact=" + contact +
                ", objective='" + objective + '\'' +
                ", projects=" + projects +
                ", skills=" + skills +
                ", workExperience=" + workExperience +
                ", education=" + education +
                ", achievements=" + achievements +
                '}';
    }

    // Inner Classes for Contact, Project, WorkExperience, and Education
    public static class Contact {
        private String email;
        private String phone;

        public Contact() {
        }

        public Contact(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        @Override
        public String toString() {
            return "Contact{" +
                    "email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    '}';
        }
    }

    public static class Project {
        private String title;
        private String description;
        private String link;
        private String githubLink;

        public Project() {
        }

        public Project(String title, String description, String link, String githubLink) {
            this.title = title;
            this.description = description;
            this.link = link;
            this.githubLink = githubLink;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getGithubLink() {
            return githubLink;
        }

        public void setGithubLink(String githubLink) {
            this.githubLink = githubLink;
        }

        @Override
        public String toString() {
            return "Project{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", link='" + link + '\'' +
                    ", githubLink='" + githubLink + '\'' +
                    '}';
        }
    }

    public static class WorkExperience {
        private String title;
        private String company;
        private String duration;
        private List<String> responsibilities;

        public WorkExperience() {
        }

        public WorkExperience(String title, String company, String duration, List<String> responsibilities) {
            this.title = title;
            this.company = company;
            this.duration = duration;
            this.responsibilities = responsibilities;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public List<String> getResponsibilities() {
            return responsibilities;
        }

        public void setResponsibilities(List<String> responsibilities) {
            this.responsibilities = responsibilities;
        }

        @Override
        public String toString() {
            return "WorkExperience{" +
                    "title='" + title + '\'' +
                    ", company='" + company + '\'' +
                    ", duration='" + duration + '\'' +
                    ", responsibilities=" + responsibilities +
                    '}';
        }
    }

    public static class Education {
        private String degree;
        private String institution;
        private String year;

        public Education() {
        }

        public Education(String degree, String institution, String year) {
            this.degree = degree;
            this.institution = institution;
            this.year = year;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getInstitution() {
            return institution;
        }

        public void setInstitution(String institution) {
            this.institution = institution;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        @Override
        public String toString() {
            return "Education{" +
                    "degree='" + degree + '\'' +
                    ", institution='" + institution + '\'' +
                    ", year='" + year + '\'' +
                    '}';
        }
    }
}
