Main Requirements
1. Execution of training application WITHOUT DOCKER and directly on the instance.
2. Execution of prediction application WITHOUT DOCKER and directly on the instance.
3. Execution of prediction application WITH DOCKER.

Prerequisites:
A. Installing Java and Apache Spark on EC2 at EC2 instance (Amazon Linux)
 -  Update System Packages: sudo yum update
 -  Install Java: sudo yum install -y java-1.8.0-openjdk-devel
 -  Download Apache Spark:curl -SL https://archive.apache.org/dist/spark/spark-3.4.1/spark-3.4.1-bin-hadoop3.tgz -o spark-3.4.1-bin-hadoop3.tgz
 -  Extract Apache Spark:sudo tar xzf spark-3.4.1-bin-hadoop3.tgz -C /opt/
 -  Set Environment Variables:
   - Set the SPARK_HOME environment variable:export SPARK_HOME=/opt/spark-3.4.1-bin-hadoop3
   - Add Spark's JARs to the CLASSPATH:export CLASSPATH=$CLASSPATH:$SPARK_HOME/jars/*
- Verify Installations:
- Check Java installation:java -version
- Check Spark installation by running a Spark shell: $SPARK_HOME/bin/spark-shell
- Search for Available Java Packages:
- First, search the repositories for available Java packages:sudo yum search java | grep openjdkThis command will list available Java packages. - Look for a JDK package that suits your needs (preferably version 1.8 or newer).


    Let’s check Linus version:
[ec2-user@ip-172-31-87-80 ~]$ uname -a
Linux ip-172-31-87-80.ec2.internal 6.1.61-85.141.amzn2023.x86_64 #1 SMP PREEMPT_DYNAMIC Wed Nov  8 00:39:18 UTC 2023 x86_64 x86_64 x86_64 GNU/Linux
[ec2-user@ip-172-31-87-80 ~]$ hostnamectl
 Static hostname: ip-172-31-87-80.ec2.internal
       Icon name: computer-vm
         Chassis: vm 🖴
      Machine ID: efb5ef96ad2d4b9da69519c75330b822
         Boot ID: e14fa64364c546abb2a0922d6d6b51f4
  Virtualization: xen
Operating System: Amazon Linux 2023
     CPE OS Name: cpe:2.3:o:amazon:amazon_linux:2023
          Kernel: Linux 6.1.61-85.141.amzn2023.x86_64
    Architecture: x86-64
 Hardware Vendor: Xen
  Hardware Model: HVM domU
Firmware Version: 4.11.amazon

Update the System:
First, update your system to ensure all your packages are up to date: sudo yum update -y
Search for Available Java Packages:
You can search for available Java packages by running: sudo yum list java*
This command will list all Java-related packages available for installation. Look for the OpenJDK packages.
it looks like Amazon Corretto, Amazon's own distribution of the OpenJDK, is available for installation. 
Install Amazon Corretto 8: sudo yum install -y java-1.8.0-amazon-corretto
Install Java Development Kit (JDK):
Once you've identified the appropriate package, install it using yum. 
For example, if you find java-1.8.0-openjdk, you can install it with:sudo yum install -y java-1.8.0-openjdk
Verify the Installation: After installation, you can verify that Java is installed correctly by running: java -version


B. Understanding of Installing Docker steps on EC2
 - Connected to the EC2 instance, 
   - update the package repository: sudo yum update -y (Amazon Linux) 
   - Install Docker: sudo yum install docker (Amazon Linux) or sudo apt-get install docker.io (Ubuntu).
   - Start the Docker service: sudo service docker start
   - Add the ec2-user to the docker group to execute Docker commands without using sudo: sudo usermod -a -G docker ec2-user

C. Github Settings - Configuring Docker Hub Credentials in GitHub Repository
- Add Docker Hub Username as a Secret:
  - Go to your GitHub repository's "Settings".
  - Navigate to "Security" > "Secrets".
  - Click "New repository secret" and name it "DOCKERHUB_USERNAME".
  - Enter your Docker Hub username in the "Value" field and save it.
- Add Docker Hub Password as a Secret:
  - Repeat the above steps but name this secret "DOCKERHUB_PASSWORD".
  - Enter your Docker Hub password and save it.
. Docker Hub Setting



3. Execution of prediction application WITH DOCKER.

**3.1 Connect to Running EC2 Instance through SSH**

rajivkumar@MacbookProHost AWS-Spark-Wine-Prediction % ssh -i "KeyPair_EC2_PA1_01.pem" ec2-user@ec2-54-162-96-209.compute-1.amazonaws.com
The authenticity of host 'ec2-54-162-96-209.compute-1.amazonaws.com (54.162.96.209)' can't be established.
ED25519 key fingerprint is SHA256:Wugj8tBSHJ04djEkDlqVxl8XyrueW+DqlSq+5x2BGyI.
This key is not known by any other names.
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added 'ec2-54-162-96-209.compute-1.amazonaws.com' (ED25519) to the list of known hosts.
   ,     #_
   ~\_  ####_        Amazon Linux 2023
  ~~  \_#####\
  ~~     \###|
  ~~       \#/ ___   https://aws.amazon.com/linux/amazon-linux-2023
   ~~       V~' '->
    ~~~         /
      ~~._.   _/
         _/ _/
       _/m/'
[ec2-user@ip-172-31-84-5 ~]$ 

**3.2 Update the Linux AMI system and Install Java**

    - Update your system to ensure all your packages are up to date: sudo yum update -y
    - Search for Available Java Packages:sudo yum list java*
    - Install Amazon Corretto 8: sudo yum install -y java-1.8.0-amazon-corretto
    - Verify the Installation: After installation, you can verify that Java is installed correctly by running: java -version

        [ec2-user@ip-172-31-84-5 ~]$ java -version
        openjdk version "1.8.0_392"
        OpenJDK Runtime Environment Corretto-8.392.08.1 (build 1.8.0_392-b08)
        OpenJDK 64-Bit Server VM Corretto-8.392.08.1 (build 25.392-b08, mixed mode)
        [ec2-user@ip-172-31-84-5 ~]$ 

**3.3 Install Docker on EC2 (Amazon LINUX)**

    - Update the package repository: sudo yum update -y  
    - Install Docker: sudo yum install docker  
    - Start the Docker service: sudo service docker start
    - Add the ec2-user to the docker group to execute Docker commands without using sudo: sudo usermod -a -G docker ec2-user

    IMPORTANT - exit from ssh EC2 and rejoin so that you will have permission to run docker pull after the above changes. 

    [ec2-user@ip-172-31-84-5 ~]$ sudo service docker start
    Redirecting to /bin/systemctl start docker.service

**3.4 Install Apache Spark**

  - Update System Packages: sudo yum update
  - Download Apache Spark:curl -SL https://archive.apache.org/dist/spark/spark-3.4.1/spark-3.4.1-bin-hadoop3.tgz -o spark-3.4.1-bin-hadoop3.tgz
  - Extract Apache Spark:sudo tar xzf spark-3.4.1-bin-hadoop3.tgz -C /opt/
  - Set the SPARK_HOME environment variable:export SPARK_HOME=/opt/spark-3.4.1-bin-hadoop3
  - Add Spark's JARs to the CLASSPATH:export CLASSPATH=$CLASSPATH:$SPARK_HOME/jars/*
  - Check Spark installation by running a Spark shell: $SPARK_HOME/bin/spark-shell

        [ec2-user@ip-172-31-84-5 ~]$ curl -SL https://archive.apache.org/dist/spark/spark-3.4.1/spark-3.4.1-bin-hadoop3.tgz -o spark-3.4.1-bin-hadoop3.tgz
        % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                        Dload  Upload   Total   Spent    Left  Speed
        100  370M  100  370M    0     0  13.5M      0  0:00:27  0:00:27 --:--:-- 13.5M

        [ec2-user@ip-172-31-84-5 ~]$ sudo tar xzf spark-3.4.1-bin-hadoop3.tgz -C /opt/  
        [ec2-user@ip-172-31-84-5 ~]$ ls -lrt
        total 379240
        -rw-r--r--. 1 ec2-user ec2-user 388341449 Dec  8 00:38 spark-3.4.1-bin-hadoop3.tgz
        [ec2-user@ip-172-31-84-5 ~]$ 

        [ec2-user@ip-172-31-84-5 ~]$ export SPARK_HOME=/opt/spark-3.4.1-bin-hadoop3
        [ec2-user@ip-172-31-84-5 ~]$ export CLASSPATH=$CLASSPATH:$SPARK_HOME/jars/*
        [ec2-user@ip-172-31-84-5 ~]$ $SPARK_HOME/bin/spark-shell
        Setting default log level to "WARN".
        To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
        23/12/08 00:41:39 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
        Spark context Web UI available at http://ip-172-31-84-5.ec2.internal:4040
        Spark context available as 'sc' (master = local[*], app id = local-1701996100562).
        Spark session available as 'spark'.
        Welcome to
            ____              __
            / __/__  ___ _____/ /__
            _\ \/ _ \/ _ `/ __/  '_/
        /___/ .__/\_,_/_/ /_/\_\   version 3.4.1
            /_/
                
        Using Scala version 2.12.17 (OpenJDK 64-Bit Server VM, Java 1.8.0_392)
        Type in expressions to have them evaluated.
        Type :help for more information.

        scala> 
        (Press CTRL +C to comeout of it)

**3.5 Pull the Docker Images:**

    -> docker pull yourdockerhubusername/training-module:latest
    -> docker pull yourdockerhubusername/prediction-module:latest
 
     $ docker pull rajivnjit/training-module:latest
     $ docker pull rajivnjit/prediction-module:latest

    [ec2-user@ip-172-31-84-5 ~]$ docker pull rajivnjit/training-module:latest
    latest: Pulling from rajivnjit/training-module
    001c52e26ad5: Pull complete 
    d9d4b9b6e964: Pull complete 
    2068746827ec: Pull complete 
    9daef329d350: Pull complete 
    d85151f15b66: Pull complete 
    52a8c426d30b: Pull complete 
    8754a66e0050: Pull complete 
    cca611dc1e63: Pull complete 
    f6e389e02196: Pull complete 
    1af51a5ffea8: Pull complete 
    15bb4c6cd22e: Pull complete 
    e63e9cd5dc45: Pull complete 
    fb389354657d: Pull complete 
    Digest: sha256:316890f2ec9dbd4116146ee76b7e6693e87acc0bbba4ed9853428adb120d2989
    Status: Downloaded newer image for rajivnjit/training-module:latest
    docker.io/rajivnjit/training-module:latest
    [ec2-user@ip-172-31-84-5 ~]$ 

    [ec2-user@ip-172-31-84-5 ~]$ docker pull rajivnjit/prediction-module:latest
    latest: Pulling from rajivnjit/prediction-module
    001c52e26ad5: Already exists 
    d9d4b9b6e964: Already exists 
    2068746827ec: Already exists 
    9daef329d350: Already exists 
    d85151f15b66: Already exists 
    52a8c426d30b: Already exists 
    8754a66e0050: Already exists 
    cca611dc1e63: Already exists 
    f6e389e02196: Already exists 
    1af51a5ffea8: Already exists 
    b39a61e109fb: Pull complete 
    6529ddce31bc: Pull complete 
    27dd111d6ed0: Pull complete 
    fb389354657d: Pull complete 
    Digest: sha256:b1021d817426c3c3cafde9df9d872806ddfe7dfc922ca843005b1cc4430cd591
    Status: Downloaded newer image for rajivnjit/prediction-module:latest
    docker.io/rajivnjit/prediction-module:latest
    [ec2-user@ip-172-31-84-5 ~]$ 

**3.6 Run the Training Module:** 

    Important note: 
    a. Docker container for the Training Module requires three arguments 
       - a configuration file path, 
       - training dataset path, and 
       - model output directory path
       
        You need to include these arguments when running the Docker command.

    b. Let's create required Directory Structure at EC2 and bring the required Config file, Training Files and model output Directory

        At EC2
            $ sudo mkdir -p prediction-results/
            $ sudo chown -R ec2-user:ec2-user prediction-results/

            $ sudo mkdir -p Datasets/
            $ sudo chown -R ec2-user:ec2-user Datasets/
        
            $ sudo mkdir -p /app/data
            $ sudo chown -R ec2-user:ec2-user /app/data/

            [ec2-user@ip-172-31-84-5 ~]$ sudo mkdir -p /app/data/Datasets/
            [ec2-user@ip-172-31-84-5 ~]$ sudo chown -R ec2-user:ec2-user /app/data/Datasets/

            $ cp spark-config.properties /app/data/


        From Local Machine or Github Training Module, Copy SparkConfig to EC2, Training and Validation CSV
        (I have copied from Local to EC2)

        rajivkumar@MacbookProHost AWS-Spark-Wine-Prediction % scp -i KeyPair_EC2_PA1_01.pem Datasets/TrainingDataset.csv ec2-user@ec2-54-162-96-209.compute-1.amazonaws.com:/home/ec2-user/Datasets
   
        rajivkumar@MacbookProHost AWS-Spark-Wine-Prediction % scp -i KeyPair_EC2_PA1_01.pem Datasets/ValidationDataset.csv ec2-user@ec2-54-162-96-209.compute-1.amazonaws.com:/home/ec2-user/Datasets 

        Make Sure that you have below Directory and File Structure :  the files are correctly mounted in the Docker container. 
        The paths /app/data/spark-config.properties, /app/data/Datasets/TrainingDataset.csv, and /app/data/Datasets/ValidationDataset.csv inside the container have the necessary files.

            [ec2-user@ip-172-31-84-5 data]$ pwd
            /app/data
            [ec2-user@ip-172-31-84-5 data]$ ls -lrt
            total 4
            -rw-r--r--. 1 ec2-user ec2-user 410 Dec  8 01:43 spark-config.properties
            drwxr-xr-x. 2 ec2-user ec2-user  62 Dec  8 01:45 Datasets
            [ec2-user@ip-172-31-84-5 data]$ ls -lrt Datasets/
            total 80
            -rw-r--r--. 1 ec2-user ec2-user  8760 Dec  8 01:45 ValidationDataset.csv
            -rw-r--r--. 1 ec2-user ec2-user 68804 Dec  8 01:45 TrainingDataset.csv
            [ec2-user@ip-172-31-84-5 data]$ 


    $  docker run -v /home/ec2-user:/app/data rajivnjit/training-module:latest /app/data/spark-config.properties /app/data/Datasets/TrainingDataset.csv /app/data/Datasets/ValidationDataset.csv

    docker run -v /home/ec2-user:/app/data rajivnjit/training-module:latest /app/data/spark-config.properties /app/data/Datasets/TrainingDataset.csv /app/data/Datasets/ValidationDataset.csv


    Note - Above command correctly maps our EC2 instance's /home/ec2-user directory to /app/data inside the container, and the files seem to be accessible at the expected paths within the container.






