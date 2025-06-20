.PHONY: build run clean generate

# Переменные
JAR_NAME = class-journal-1.0-SNAPSHOT-jar-with-dependencies.jar
TARGET_DIR = target
JAR_PATH = $(TARGET_DIR)/$(JAR_NAME)

# Сборка проекта
build:
	mvn clean package

# Запуск приложения
run:
	@if [ ! -f "$(JAR_PATH)" ]; then \
		echo "JAR not found. Building project first..."; \
		make build; \
	fi
	java -jar $(JAR_PATH)

# Очистка
clean:
	mvn clean
	rm -rf $(TARGET_DIR)

# Генерация тестовых Excel-файлов
generate:
	@if [ ! -f "$(TARGET_DIR)/$(JAR_NAME)" ]; then \
		echo "JAR not found. Building project first..."; \
		make build; \
	fi
	java -cp $(TARGET_DIR)/$(JAR_NAME) com.journal.util.ExcelDataGenerator 

rebuild: clean build