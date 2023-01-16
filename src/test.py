import os
import time
import subprocess

def main():
    folder = ["a", "b", "c"]
    os.system("make")
    print()
    thread_time = [1, 1, 1]
    start_time = 0
    end_time = 0
    failed = False

    command_product_diff = ['diff', 'product1.txt', 'product2.txt']
    command_order_diff = ['diff', 'order1.txt', 'order2.txt']

    for threads in [1, 2, 4]:
        start_time = time.time()

        print(f'Run tests with {threads} threads')
        for i in range(0,3):
            sort_output(folder, threads, i)

            n1, n2 = check_difference(command_product_diff, command_order_diff)

            if (n1 == 0 and n2 == 0):
                print(f'Test {folder[i]} passed')

            else:
                print(f'Test {folder[i]} failed')
                print_difference(n1, n2)
                failed = True
                break
        
        if (failed):
            break
        
        end_time = time.time()
        
        thread_time[threads >> 1] = end_time - start_time
        print("Runs in %.2f seconds for %i thread/s" % (thread_time[threads >> 1], threads))
        print()

        os.system("rm *.txt")
        os.system(f'find ./sample_data/input -name "*_out.txt" -type f -delete')
        
    print("Scalability 1-2 %.2f" % (thread_time[0] / thread_time[1]))
    print("Scalability 1-4 %.2f" % (thread_time[0] / thread_time[2]))

    clean()

def clean():    
    print()
    os.system("make clean")



def check_difference(command_product_diff, command_order_diff):
    p1 = subprocess.Popen(command_product_diff, stdout=subprocess.PIPE)
    n1 = subprocess.Popen(['wc', '-l'], stdin=p1.stdout, stdout=subprocess.PIPE)
    p1.stdout.close()

    p2 = subprocess.Popen(command_order_diff, stdout=subprocess.PIPE)
    n2 = subprocess.Popen(['wc', '-l'], stdin=p2.stdout, stdout=subprocess.PIPE)
    p2.stdout.close()

    return int(n1.communicate()[0]), int(n2.communicate()[0])

def print_difference(n1, n2):
    if (n1 != 0):
        os.system("diff product1.txt product2.txt")
        print("\nStudents product output\n----------------------------------------")
        os.system("cat product1.txt")
        print("----------------------------------------")
        os.system("cat product2.txt")
        print()

    if (n2 != 0):
        os.system("diff order1.txt order2.txt")
        print("\nStudents order output\n----------------------------------------")
        os.system("cat order1.txt")
        print("----------------------------------------")
        os.system("cat order2.txt")
        print()


def sort_output(folder, threads, i):
    input = "./sample_data/input/input_" + folder[i] + "/"
    output = "./sample_data/output/output_" + folder[i] + "/"

    os.system(f'java Tema2 {input} {threads}')
    os.system(f'cat ./order_products_out.txt | sort > product1.txt')
    os.system(f'cat ./orders_out.txt | sort > order1.txt')

    os.system(f'cat {output}/order_products_out.txt | sort > product2.txt')
    os.system(f'cat {output}/orders_out.txt | sort > order2.txt')

if __name__ == "__main__":
    main()