# Use a base image with Python
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the requirements.txt file into the container
COPY requirements.txt ./

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of your Locust files into the container
COPY . .

# Expose the Locust web interface port
EXPOSE 8089

# Command to run when the container starts
CMD ["locust", "-f", "locustfile.py"]
