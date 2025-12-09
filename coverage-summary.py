#!/usr/bin/env python3
import xml.etree.ElementTree as ET
import sys

def parse_jacoco_report(xml_file):
    """解析 JaCoCo XML 报告并提取覆盖率数据"""
    try:
        tree = ET.parse(xml_file)
        root = tree.getroot()

        # 获取根级别的计数器（整体覆盖率）
        counters = {}
        for counter in root.findall('counter'):
            counter_type = counter.get('type')
            covered = int(counter.get('covered', 0))
            missed = int(counter.get('missed', 0))
            total = covered + missed

            if total > 0:
                percentage = (covered / total) * 100
                counters[counter_type] = {
                    'covered': covered,
                    'missed': missed,
                    'total': total,
                    'percentage': percentage
                }

        return counters
    except Exception as e:
        print(f"Error parsing XML: {e}")
        return None

def print_coverage_report(counters):
    """打印格式化的覆盖率报告"""
    print("\n" + "=" * 50)
    print("         JaCoCo 代码覆盖率报告")
    print("=" * 50)

    metrics = [
        ('INSTRUCTION', '指令覆盖率'),
        ('BRANCH', '分支覆盖率'),
        ('LINE', '行覆盖率'),
        ('METHOD', '方法覆盖率'),
        ('CLASS', '类覆盖率'),
        ('COMPLEXITY', '复杂度覆盖率')
    ]

    for metric_key, metric_name in metrics:
        if metric_key in counters:
            data = counters[metric_key]
            bar_length = 20
            filled = int(bar_length * data['percentage'] / 100)
            bar = '█' * filled + '░' * (bar_length - filled)

            print(f"\n{metric_name}:")
            print(f"  [{bar}] {data['percentage']:.1f}%")
            print(f"  覆盖: {data['covered']}/{data['total']}")
            if data['missed'] > 0:
                print(f"  未覆盖: {data['missed']}")

    print("\n" + "=" * 50)

    # 整体评价
    if 'LINE' in counters:
        line_coverage = counters['LINE']['percentage']
        if line_coverage >= 80:
            print("✅ 优秀！代码覆盖率达到 80% 以上")
        elif line_coverage >= 60:
            print("⚠️  良好！代码覆盖率达到 60% 以上")
        else:
            print("❌ 需要改进！代码覆盖率低于 60%")

    print("=" * 50)

if __name__ == "__main__":
    xml_file = "build/reports/jacoco/test/jacocoTestReport.xml"

    counters = parse_jacoco_report(xml_file)
    if counters:
        print_coverage_report(counters)

        # 返回状态码（可用于 CI/CD）
        line_coverage = counters.get('LINE', {}).get('percentage', 0)
        sys.exit(0 if line_coverage >= 60 else 1)