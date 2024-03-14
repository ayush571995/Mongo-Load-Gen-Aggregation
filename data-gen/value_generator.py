from faker import Faker
from datetime import datetime
import random,time


fake = Faker()

# Function to generate a value based on the specified type
def generate_value(data_type):
    parts = data_type.split(':')
    base_type = parts[0]

    if base_type == 't_name':
        return fake.name()
    elif base_type == 't_email':
        return fake.email()
    elif base_type == 't_address':
        return fake.address()
    elif base_type == 't_date':
        start_date = parts[1] if len(parts) > 1 else '-5y'
        end_date = parts[2] if len(parts) > 2 else 'today'
        return fake.date_between(start_date=start_date, end_date=end_date).isoformat()
    elif base_type == 't_int':
        min_val = int(parts[1]) if len(parts) > 1 else 1
        max_val = int(parts[2]) if len(parts) > 2 else 100
        return fake.random_int(min=min_val, max=max_val)
    elif base_type == 't_double':
        min_val = float(parts[1]) if len(parts) > 1 else 0.0
        max_val = float(parts[2]) if len(parts) > 2 else 100.0
        return random.uniform(min_val, max_val)
    elif base_type == 't_bool':
        return fake.boolean()
    elif base_type == 't_latitude':
        return float(fake.latitude())
    elif base_type == 't_longitude':
        return float(fake.longitude())
    elif base_type == 't_unix_epoch':
        start_date = datetime.strptime(parts[1], "%Y-%m-%d") if len(parts) > 1 else datetime.now() - datetime.timedelta(days=365 * 5)
        end_date = datetime.strptime(parts[2], "%Y-%m-%d") if len(parts) > 2 else datetime.now()
        random_date = fake.date_time_between_dates(datetime_start=start_date, datetime_end=end_date)
        return int(time.mktime(random_date.timetuple()))
    elif base_type == 't_array':
        array_length = int(parts[1]) if len(parts) > 1 else 5  # Default length if not specified
        element_type = parts[2] if len(parts) > 2 else 't_name'  # Default element type if not specified
        return [generate_value(element_type) for _ in range(array_length)]
    elif base_type == 't_phone':
        return fake.phone_number()
    elif base_type == 't_city':
        return fake.city()
    elif base_type == 't_country':
        return fake.country()
    elif base_type == 't_pincode':
        return fake.postcode()
    else:
        return "Unsupported data type"

