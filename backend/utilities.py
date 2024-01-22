from datetime import datetime

def get_timestamp(item):
    return datetime.strptime(item[1], '%Y-%m-%d %H:%M:%S')