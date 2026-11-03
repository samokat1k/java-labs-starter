package edu.course.lab03;

import java.util.Objects;


interface DiscountPolicy {
    double apply(double price);
}

class NoDiscount implements DiscountPolicy {
    @Override
    public double apply(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price must not be negative: " + price);
        }
        return price;
    }
}

class PercentDiscount implements DiscountPolicy {
    private final double percent;

    PercentDiscount(double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Percent must be in [0, 100]: " + percent);
        }
        this.percent = percent;
    }

    @Override
    public double apply(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price must not be negative: " + price);
        }
        return price * (100 - percent) / 100;
    }
}

class PriceCalculator {
    private final DiscountPolicy policy;

    PriceCalculator(DiscountPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    double calculate(double price) {
        return policy.apply(price);
    }
}


enum TaskStatus {
    TODO, IN_PROGRESS, DONE
}

class ProjectTask {
    private final String title;
    private final int effortHours;
    private final TaskStatus status;

    ProjectTask(String title, int effortHours, TaskStatus status) {
        if (effortHours < 0) {
            throw new IllegalArgumentException("Effort must not be negative: " + effortHours);
        }
        this.title = Objects.requireNonNull(title, "title");
        this.status = Objects.requireNonNull(status, "status");
        this.effortHours = effortHours;
    }

    String getTitle() { return title; }
    int getEffortHours() { return effortHours; }
    TaskStatus getStatus() { return status; }
}

interface PriorityPolicy {
    int calculatePriority(ProjectTask task); // чем больше число, тем выше приоритет
}

class EffortPriorityPolicy implements PriorityPolicy {
    @Override
    public int calculatePriority(ProjectTask task) {
        return -task.getEffortHours();
    }
}

class StatusPriorityPolicy implements PriorityPolicy {
    @Override
    public int calculatePriority(ProjectTask task) {
        switch (task.getStatus()) {
            case IN_PROGRESS: return 3;
            case TODO:        return 2;
            case DONE:        return 1;
            default: throw new IllegalStateException("Unknown status: " + task.getStatus());
        }
    }
}

class TaskPrioritizer {
    private final PriorityPolicy policy;

    TaskPrioritizer(PriorityPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    ProjectTask pickHigher(ProjectTask a, ProjectTask b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        return policy.calculatePriority(b) > policy.calculatePriority(a) ? b : a;
    }
}


public class lab03 {
    public static void main(String[] args) {
        System.out.println(new PriceCalculator(new NoDiscount()).calculate(100));       
        System.out.println(new PriceCalculator(new PercentDiscount(20)).calculate(100)); 
        ProjectTask quick = new ProjectTask("quick", 2, TaskStatus.TODO);
        ProjectTask big = new ProjectTask("big", 20, TaskStatus.IN_PROGRESS);

        System.out.println(new TaskPrioritizer(new EffortPriorityPolicy()).pickHigher(quick, big).getTitle()); 
        System.out.println(new TaskPrioritizer(new StatusPriorityPolicy()).pickHigher(quick, big).getTitle()); 
    }
}