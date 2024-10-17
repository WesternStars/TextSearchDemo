package org.bigId.matcher;

public record Position(int lineOffset, int charOffset) {

    @Override
    public String toString() {
        return String.format("[lineOffset=%d, charOffset=%d]", lineOffset, charOffset);
    }

}
