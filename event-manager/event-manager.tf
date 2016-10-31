
provider "aws" {
  region                  = "${var.aws_region}"
}

variable "latcraft_function_dist" {
  default = "build/distributions/event-manager.zip"
}

resource "aws_s3_bucket" "latcraft_code" {
  bucket                  = "latcraft-code"
  acl                     = "private"
}

resource "aws_s3_bucket" "latcraft_images" {
  bucket                  = "latcraft-images"
  acl                     = "private"
}


//    _                 _         _
//   | |               | |       | |
//   | | __ _ _ __ ___ | |__   __| | __ _
//   | |/ _` | '_ ` _ \| '_ \ / _` |/ _` |
//   | | (_| | | | | | | |_) | (_| | (_| |
//   |_|\__,_|_| |_| |_|_.__/ \__,_|\__,_|
//
//

resource "aws_iam_role" "latcraft_lambda_executor" {
  name                    = "latcraft_lambda_executor"
  assume_role_policy      = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Effect": "Allow",
      "Principal": {
        "Service": "apigateway.amazonaws.com"
      }
    },
    {
      "Action": "sts:AssumeRole",
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      }
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "latcraft_lambda_executor_policy" {
    name                  = "devternity_lambda_executor_policy"
    role                  = "${aws_iam_role.latcraft_lambda_executor.id}"
    policy                = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:DescribeLogGroups",
          "logs:DescribeLogStreams",
          "logs:PutLogEvents",
          "logs:GetLogEvents",
          "logs:FilterLogEvents"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket"],
      "Resource": ["${aws_s3_bucket.latcraft_images.arn}"]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:PutObjectAcl",
        "s3:GetObject",
        "s3:GetObjectAcl"
      ],
      "Resource": ["${aws_s3_bucket.latcraft_images.arn}/*"]
    }
  ]
}
EOF
}

