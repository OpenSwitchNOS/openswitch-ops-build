import subprocess

def run(command):
    popen = subprocess.Popen(command, stdout=subprocess.PIPE,
                             shell=True)
    return iter(popen.stdout.readline, b"")

def pytest_exception_interact(node, call, report):
    if report.outcome == "failed":
        output = []
        for line in run(["tail -n 2000 /var/log/syslog"]):
            line = line.decode('utf-8')
            output.append(str(line))
        output = "".join(output)
        with open("container.log", "w+", 1) as _file:
            _file.write(str(output))
